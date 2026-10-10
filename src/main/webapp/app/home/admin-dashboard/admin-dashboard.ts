import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import dayjs from 'dayjs/esm';
import { forkJoin } from 'rxjs';

import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { ILigneCommande } from 'app/entities/ligne-commande/ligne-commande.model';
import { LigneCommandeService } from 'app/entities/ligne-commande/service/ligne-commande.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';

interface CommandeStatutStat {
  key: string;
  value: number;
}

interface TopProduit {
  nom: string;
  categorie: string;
  ventes: number;
  revenu: number;
  part: number;
}

interface StockAlerte {
  produit: string;
  variante: string;
  stock: number;
}

interface CommandeRecente {
  numero: string;
  cliente: string;
  date: string;
  montant: number;
  statut: string;
  paiement: string;
}

interface AreaPath {
  line: string;
  area: string;
}

interface PeriodStats {
  ca: number;
  count: number;
  annulees: number;
  panier: number;
  clientes: number;
}

const STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  CONFIRMEE: 'Confirmée',
  EN_PREPARATION: 'En préparation',
  EXPEDIEE: 'Expédiée',
  LIVREE: 'Livrée',
  ANNULEE: 'Annulée',
};

const STATUT_COLORS: Record<string, string> = {
  EN_ATTENTE: '#B9895A',
  CONFIRMEE: '#7C8CE1',
  EN_PREPARATION: '#B9895A',
  EXPEDIEE: '#E11D74',
  LIVREE: '#3F7D5C',
  ANNULEE: '#B3453F',
};

const MODE_PAIEMENT_LABELS: Record<string, string> = {
  WAVE: 'Wave',
  ORANGE_MONEY: 'Orange Money',
  SUR_PLACE: 'Sur place',
};

const PERIODE_JOURS = 30;
const SEUIL_STOCK_FAIBLE = 3;

@Component({
  selector: 'jhi-admin-dashboard',
  templateUrl: './admin-dashboard.html',
  styleUrl: './admin-dashboard.scss',
  imports: [RouterLink, FontAwesomeModule],
})
export default class AdminDashboard implements OnInit {
  readonly isLoading = signal(true);
  readonly hasError = signal(false);

  private readonly commandes = signal<ICommande[]>([]);
  private readonly variantes = signal<IVarianteProduit[]>([]);
  private readonly produits = signal<IProduit[]>([]);
  private readonly lignes = signal<ILigneCommande[]>([]);

  private readonly commandeService = inject(CommandeService);
  private readonly varianteService = inject(VarianteProduitService);
  private readonly produitService = inject(ProduitService);
  private readonly ligneService = inject(LigneCommandeService);

  // ---------------------------------------------------------------------
  // Périodes : 30 derniers jours vs 30 jours précédents
  // ---------------------------------------------------------------------
  private readonly commandesPeriode = computed(() => {
    const debut = dayjs().subtract(PERIODE_JOURS, 'day');
    return this.commandes().filter(c => c.dateCommande && c.dateCommande.isAfter(debut));
  });

  private readonly commandesPrecedentes = computed(() => {
    const fin = dayjs().subtract(PERIODE_JOURS, 'day');
    const debut = dayjs().subtract(PERIODE_JOURS * 2, 'day');
    return this.commandes().filter(c => c.dateCommande && c.dateCommande.isAfter(debut) && c.dateCommande.isBefore(fin));
  });

  readonly stats = computed(() => this.computeStats(this.commandesPeriode()));
  private readonly statsPrecedentes = computed(() => this.computeStats(this.commandesPrecedentes()));

  readonly deltas = computed(() => {
    const cur = this.stats();
    const prev = this.statsPrecedentes();
    return {
      ca: this.evolution(cur.ca, prev.ca),
      commandes: this.evolution(cur.count, prev.count),
      clientes: this.evolution(cur.clientes, prev.clientes),
      panier: this.evolution(cur.panier, prev.panier),
      annulees: this.evolution(cur.annulees, prev.annulees),
    };
  });

  // ---------------------------------------------------------------------
  // Graphiques
  // ---------------------------------------------------------------------
  private readonly revenuTrend = computed(() => {
    const parJour = new Map<string, number>();
    for (const c of this.commandesPeriode()) {
      if (c.statutCommande === 'ANNULEE' || !c.dateCommande) {
        continue;
      }
      const cle = c.dateCommande.format('YYYY-MM-DD');
      parJour.set(cle, (parJour.get(cle) ?? 0) + Number(c.montantTotal ?? 0));
    }
    const valeurs: number[] = [];
    for (let i = PERIODE_JOURS - 1; i >= 0; i--) {
      valeurs.push(parJour.get(dayjs().subtract(i, 'day').format('YYYY-MM-DD')) ?? 0);
    }
    return valeurs;
  });

  readonly heroChart = computed<AreaPath>(() => this.buildAreaPath(this.revenuTrend(), 300, 80));
  readonly trendChart = computed<AreaPath>(() => this.buildAreaPath(this.revenuTrend(), 620, 170));

  readonly commandesParStatut = computed<CommandeStatutStat[]>(() => {
    const compteur = new Map<string, number>();
    for (const c of this.commandesPeriode()) {
      const cle = c.statutCommande ?? 'EN_ATTENTE';
      compteur.set(cle, (compteur.get(cle) ?? 0) + 1);
    }
    return Object.keys(STATUT_LABELS)
      .map(key => ({ key, value: compteur.get(key) ?? 0 }))
      .filter(item => item.value > 0);
  });

  readonly totalCommandesStatut = computed(() => this.commandesParStatut().reduce((sum, item) => sum + item.value, 0));

  readonly donutGradient = computed(() => {
    const total = this.totalCommandesStatut();
    if (total === 0) {
      return '#eadfda';
    }
    let cumulative = 0;
    const stops = this.commandesParStatut().map(item => {
      const start = (cumulative / total) * 360;
      cumulative += item.value;
      const end = (cumulative / total) * 360;
      return `${this.statutColor(item.key)} ${start}deg ${end}deg`;
    });
    return `conic-gradient(${stops.join(', ')})`;
  });

  // ---------------------------------------------------------------------
  // Listes
  // ---------------------------------------------------------------------
  readonly commandesRecentes = computed<CommandeRecente[]>(() =>
    [...this.commandes()]
      .sort((a, b) => (b.dateCommande?.valueOf() ?? 0) - (a.dateCommande?.valueOf() ?? 0))
      .slice(0, 6)
      .map(c => ({
        numero: c.numero ?? `#${c.id}`,
        cliente: c.clienteProfile?.telephone ?? (c.clienteProfile ? `#${c.clienteProfile.id}` : '—'),
        date: c.dateCommande ? c.dateCommande.toDate().toLocaleDateString('fr-FR', { day: '2-digit', month: 'short' }) : '—',
        montant: Number(c.montantTotal ?? 0),
        statut: c.statutCommande ?? 'EN_ATTENTE',
        paiement: c.modePaiement ? (MODE_PAIEMENT_LABELS[c.modePaiement] ?? c.modePaiement) : '—',
      })),
  );

  readonly stocksFaibles = computed<StockAlerte[]>(() => {
    const nomsProduits = new Map<number, string>();
    for (const p of this.produits()) {
      if (p.id != null && p.nom) {
        nomsProduits.set(p.id, p.nom);
      }
    }
    return this.variantes()
      .filter(v => (v.stock ?? 0) <= SEUIL_STOCK_FAIBLE)
      .sort((a, b) => (a.stock ?? 0) - (b.stock ?? 0))
      .slice(0, 6)
      .map(v => {
        const taille = (v.taille as any)?.libelle as string | undefined;
        const couleur = (v.couleur as any)?.libelle as string | undefined;
        return {
          produit: (v.produit?.id != null ? nomsProduits.get(v.produit.id) : undefined) ?? `Produit #${v.produit?.id ?? '?'}`,
          variante: [taille, couleur].filter(Boolean).join(' · ') || `Variante #${v.id}`,
          stock: v.stock ?? 0,
        };
      });
  });

  readonly topProduits = computed<TopProduit[]>(() => {
    // On ne garde que les lignes des commandes de la période, hors annulées
    const idsValides = new Set(
      this.commandesPeriode()
        .filter(c => c.statutCommande !== 'ANNULEE')
        .map(c => c.id),
    );

    const produitsParId = new Map<number, IProduit>();
    for (const p of this.produits()) {
      if (p.id != null) {
        produitsParId.set(p.id, p);
      }
    }

    const agregat = new Map<number, { ventes: number; revenu: number }>();
    for (const l of this.lignes()) {
      const ligne = l as any;
      if (!idsValides.has(ligne.commande?.id)) {
        continue;
      }
      const produitId: number | undefined = ligne.varianteProduit?.produit?.id ?? ligne.produit?.id;
      if (produitId == null) {
        continue;
      }
      const quantite = Number(ligne.quantite ?? 0);
      const revenu = Number(ligne.prixUnitaire ?? 0) * quantite;
      const cur = agregat.get(produitId) ?? { ventes: 0, revenu: 0 };
      agregat.set(produitId, { ventes: cur.ventes + quantite, revenu: cur.revenu + revenu });
    }

    const top = [...agregat.entries()].sort((a, b) => b[1].ventes - a[1].ventes).slice(0, 5);
    const max = top[0]?.[1].ventes || 1;

    return top.map(([produitId, v]) => {
      const p = produitsParId.get(produitId);
      return {
        nom: p?.nom ?? `Produit #${produitId}`,
        categorie: p?.categorie?.nom ?? '—',
        ventes: v.ventes,
        revenu: v.revenu,
        part: Math.round((v.ventes / max) * 100),
      };
    });
  });

  ngOnInit(): void {
    forkJoin({
      commandes: this.commandeService.query({ page: 0, size: 1000, sort: ['dateCommande,desc'] }),
      variantes: this.varianteService.query({ page: 0, size: 1000 }),
      produits: this.produitService.query({ page: 0, size: 1000 }),
      lignes: this.ligneService.query({ page: 0, size: 5000 }),
    }).subscribe({
      next: res => {
        this.commandes.set(res.commandes.body ?? []);
        this.variantes.set(res.variantes.body ?? []);
        this.produits.set(res.produits.body ?? []);
        this.lignes.set(res.lignes.body ?? []);
        this.isLoading.set(false);
      },
      error: () => {
        this.hasError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  statutLabel(key: string): string {
    return STATUT_LABELS[key] ?? key;
  }

  statutColor(key: string): string {
    return STATUT_COLORS[key] ?? '#8B7A73';
  }

  statutPart(item: CommandeStatutStat): number {
    const total = this.totalCommandesStatut();
    return total === 0 ? 0 : Math.round((item.value / total) * 100);
  }

  formatFcfa(montant: number): string {
    return `${Math.round(montant).toLocaleString('fr-FR')} F`;
  }

  formatDelta(valeur: number | null): string {
    if (valeur === null) {
      return '—';
    }
    const signe = valeur > 0 ? '+' : '';
    return `${signe}${valeur.toLocaleString('fr-FR', { maximumFractionDigits: 1 })} %`;
  }

  /** Pour "annulées", une hausse est une mauvaise nouvelle : on inverse. */
  deltaClass(valeur: number | null, inverse = false): string {
    if (valeur === null || valeur === 0) {
      return '';
    }
    const positif = inverse ? valeur < 0 : valeur > 0;
    return positif ? 'up' : 'down';
  }

  deltaIcon(valeur: number | null): string {
    return valeur !== null && valeur < 0 ? 'arrow-down' : 'arrow-up';
  }

  initiales(nomComplet: string): string {
    return nomComplet
      .replace('#', '')
      .split(' ')
      .map(mot => mot[0])
      .join('')
      .slice(0, 2)
      .toUpperCase();
  }

  private computeStats(commandes: ICommande[]): PeriodStats {
    const valides = commandes.filter(c => c.statutCommande !== 'ANNULEE');
    const ca = valides.reduce((sum, c) => sum + Number(c.montantTotal ?? 0), 0);
    const clientes = new Set(valides.map(c => c.clienteProfile?.id).filter(id => id != null));
    return {
      ca,
      count: commandes.length,
      annulees: commandes.length - valides.length,
      panier: valides.length > 0 ? ca / valides.length : 0,
      clientes: clientes.size,
    };
  }

  private evolution(actuel: number, precedent: number): number | null {
    if (precedent === 0) {
      return actuel === 0 ? 0 : null;
    }
    return ((actuel - precedent) / precedent) * 100;
  }

  private buildAreaPath(values: number[], width: number, height: number): AreaPath {
    if (values.length < 2) {
      return { line: '', area: '' };
    }
    const max = Math.max(...values);
    const min = Math.min(...values);
    const range = max - min || 1;
    const stepX = width / (values.length - 1);

    const points = values.map((v, i) => [i * stepX, height - ((v - min) / range) * height] as const);
    const line = points.map(([x, y], i) => `${i === 0 ? 'M' : 'L'}${x.toFixed(1)},${y.toFixed(1)}`).join(' ');
    const area = `${line} L${width},${height} L0,${height} Z`;

    return { line, area };
  }
}
