import { NgClass } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';

const STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  CONFIRMEE: 'Confirmée',
  EN_PREPARATION: 'En préparation',
  EXPEDIEE: 'En cours de livraison',
  LIVREE: 'Livrée',
  ANNULEE: 'Annulée',
};
const MODE_PAIEMENT_LABELS: Record<string, string> = {
  WAVE: 'Wave',
  ORANGE_MONEY: 'Orange Money',
  SUR_PLACE: 'Sur place',
};

interface MaLigne {
  id: number;
  produitId: number;
  nom: string;
  taille: string;
  couleur: string;
  quantite: number;
  prixUnitaire: number;
}

interface MaLivraison {
  adresse?: string | null;
  ville?: string | null;
  quartier?: string | null;
  telephone?: string | null;
}

interface MaCommande {
  id: number;
  numero: string;
  dateCommande: string;
  montantTotal: number;
  modePaiement: string | null;
  statutCommande: string;
  livraison: MaLivraison | null;
  lignes: MaLigne[];
}

interface ProduitListExtra {
  produitId: number;
  imageUrl: string | null;
}

/**
 * Page "Mes commandes" — réservée aux clientes connectées (voir canActivate
 * dans app.routes.ts). N'affiche QUE les commandes de la cliente connectée,
 * en lecture seule.
 *
 * Un seul appel pour les données : GET /api/commandes/mes-commandes renvoie
 * chaque commande avec ses articles et sa livraison (résolu côté serveur via
 * le token JWT). Les images viennent de GET /api/produits/list-extras (public).
 */
@Component({
  selector: 'jhi-mes-commandes',
  templateUrl: './mes-commandes.html',
  styleUrl: './mes-commandes.scss',
  imports: [RouterLink, NgClass, FontAwesomeModule],
})
export default class MesCommandes implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly http = inject(HttpClient);

  readonly account = this.accountService.account;
  readonly commandes = signal<MaCommande[]>([]);
  readonly isLoading = signal(true);
  /** produitId -> url de la première image du produit. */
  readonly images = signal<Record<number, string>>({});

  ngOnInit(): void {
    this.loadCommandes();
  }

  statutLabel(statut: string | null | undefined): string {
    return statut ? (STATUT_LABELS[statut] ?? statut) : '—';
  }

  modePaiementLabel(mode: string | null | undefined): string {
    return mode ? (MODE_PAIEMENT_LABELS[mode] ?? mode) : '—';
  }

  statutClass(statut: string | null | undefined): string {
    return statut ? `statut-${statut.toLowerCase().replace(/_/g, '-')}` : '';
  }

  formatFcfa(montant: number): string {
    return `${Number(montant).toLocaleString('fr-FR')} FCFA`;
  }

  formatDate(iso: string): string {
    return new Date(iso).toLocaleDateString('fr-FR', { day: 'numeric', month: 'long', year: 'numeric' });
  }

  adresseLivraison(livraison: MaLivraison | null): string {
    if (!livraison) {
      return '';
    }
    return [livraison.quartier, livraison.ville].filter(Boolean).join(', ');
  }

  private loadCommandes(): void {
    if (!this.account()) {
      this.isLoading.set(false);
      return;
    }
    this.http.get<MaCommande[]>('api/commandes/mes-commandes', { params: { sort: 'dateCommande,desc' } }).subscribe({
      next: commandes => {
        this.commandes.set(commandes);
        this.isLoading.set(false);
        this.loadImages(commandes);
      },
      error: () => this.isLoading.set(false),
    });
  }

  private loadImages(commandes: MaCommande[]): void {
    const ids = [...new Set(commandes.flatMap(c => c.lignes.map(l => l.produitId)))];
    if (ids.length === 0) {
      return;
    }
    let params = new HttpParams();
    ids.forEach(id => (params = params.append('ids', id)));
    this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params }).subscribe({
      next: extras => {
        const record: Record<number, string> = {};
        extras.forEach(e => {
          if (e.imageUrl) {
            record[e.produitId] = e.imageUrl;
          }
        });
        this.images.set(record);
      },
      error: () => {
        /* pas bloquant : les articles s'affichent sans image */
      },
    });
  }
}
