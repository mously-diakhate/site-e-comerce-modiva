import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { CartService } from 'app/core/cart/cart.service';
import { ICategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
// Adaptez ce chemin si votre dossier généré diffère (voir shop-home.ts).
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';

type Silhouette = 'blazer' | 'chemise' | 'robe' | 'ensemble';

interface CouleurInfo {
  libelle: string;
  codeHex: string;
}

interface ProduitSuggestion {
  id: number;
  nom: string;
  prix: number;
  tone: string;
  silhouette: Silhouette;
  imageUrl?: string;
  couleurs: CouleurInfo[];
  /** true si le stock total (toutes variantes) du produit suggéré est à 0. */
  epuise: boolean;
}

interface ProduitListExtra {
  produitId: number;
  imageUrl: string | null;
}

const TONES = ['p-tone-1', 'p-tone-2', 'p-tone-3', 'p-tone-4'];
const SILHOUETTES: Silhouette[] = ['blazer', 'chemise', 'robe', 'ensemble'];
const TAILLE_ORDER = ['XS', 'S', 'M', 'L', 'XL', '2XL', '3XL'];
/** Même seuil que shop-home / shop-catalog pour le badge "Nouveauté". */
const NOUVEAUTE_LIMIT = 4;
/** Nombre de produits proposés dans "Vous aimerez aussi". */
const SUGGESTIONS_LIMIT = 4;

/** Sépare taille et couleur dans la clé de recherche de variante (voir varianteIdByCombo). */
const COMBO_SEPARATOR = '|';

/**
 * Page produit publique : galerie, prix, description, sélection taille /
 * couleur, ajout au panier (bloqué si le produit est épuisé), détails, et
 * suggestions de produits de la même catégorie. Remplace l'écran "view"
 * généré par JHipster pour l'entité Produit (voir la route ajoutée en tête
 * de app.routes.ts).
 */
@Component({
  selector: 'jhi-produit-vue',
  templateUrl: './produit-vue.html',
  styleUrl: './produit-vue.scss',
  imports: [RouterLink, FontAwesomeModule],
})
export default class ProduitVue implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly produitService = inject(ProduitService);
  private readonly categorieService = inject(CategorieService);
  private readonly varianteProduitService = inject(VarianteProduitService);
  private readonly http = inject(HttpClient);
  protected readonly cart = inject(CartService);

  readonly isLoading = signal(true);
  readonly produit = signal<IProduit | null>(null);
  readonly imageUrl = signal<string | null>(null);
  readonly categorieNom = signal<string>('');
  readonly categorieId = signal<number | null>(null);
  readonly isNouveau = signal(false);
  readonly silhouette = signal<Silhouette>('robe');

  /**
   * Le premier paragraphe non vide de la description sert d'accroche
   * (affichée sous le titre) ; les lignes suivantes deviennent les puces de
   * la section "Détails" — il suffit de sauter une ligne dans le champ
   * "description" du produit (espace admin) pour les distinguer.
   */
  readonly descriptionCourte = signal('');
  readonly detailsBullets = signal<string[]>([]);

  readonly tailles = signal<string[]>([]);
  readonly couleurs = signal<CouleurInfo[]>([]);
  readonly selectedTaille = signal<string | null>(null);
  readonly selectedCouleur = signal<string | null>(null);

  /** Passe à true quand les variantes du produit sont chargées (évite d'afficher "Épuisé" avant d'avoir la réponse). */
  readonly variantesLoaded = signal(false);
  /** Stock total (toutes variantes) du produit. */
  readonly stockTotal = signal(0);
  /** Produit totalement épuisé : variantes chargées et stock total à 0. */
  readonly epuise = computed(() => this.variantesLoaded() && this.stockTotal() === 0);

  /** taille|couleur -> id de la VarianteProduit exacte (voir loadVariantes). */
  private varianteIdByCombo = new Map<string, number>();

  /**
   * Un produit épuisé ne peut jamais être ajouté. Sinon, une taille est
   * requise seulement si le produit en propose (idem couleur) — un produit
   * sans déclinaison de taille n'en bloque pas l'ajout au panier, par exemple.
   */
  readonly canAddToCart = computed(
    () =>
      !this.epuise() &&
      (this.tailles().length === 0 || this.selectedTaille() !== null) &&
      (this.couleurs().length === 0 || this.selectedCouleur() !== null),
  );

  readonly justAdded = signal(false);
  /** Affiché quand la combinaison taille+couleur choisie n'existe pas comme variante réelle. */
  readonly comboUnavailable = signal(false);
  readonly suggestions = signal<ProduitSuggestion[]>([]);

  ngOnInit(): void {
    // On écoute paramMap en continu (pas juste au premier chargement) : en
    // cliquant sur une suggestion, l'URL passe de /produit/12/view à
    // /produit/15/view — Angular réutilise le même composant sur cette même
    // route, donc ngOnInit() ne se redéclenche pas tout seul. Sans cet
    // abonnement, la page resterait affichée avec l'ancien produit.
    this.route.paramMap.subscribe(params => {
      const idParam = params.get('id');
      const id = idParam ? Number(idParam) : null;
      if (id != null) {
        this.resetState();
        this.loadProduit(id);
        this.loadNouveauteStatus(id);
        document.querySelector('.produit-vue')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
      }
    });
  }

  /** Remet l'état à zéro avant de charger un nouveau produit, pour éviter d'afficher un instant les données de l'ancien. */
  private resetState(): void {
    this.produit.set(null);
    this.imageUrl.set(null);
    this.categorieNom.set('');
    this.categorieId.set(null);
    this.isNouveau.set(false);
    this.descriptionCourte.set('');
    this.detailsBullets.set([]);
    this.tailles.set([]);
    this.couleurs.set([]);
    this.selectedTaille.set(null);
    this.selectedCouleur.set(null);
    this.variantesLoaded.set(false);
    this.stockTotal.set(0);
    this.varianteIdByCombo = new Map();
    this.justAdded.set(false);
    this.comboUnavailable.set(false);
    this.suggestions.set([]);
  }

  formatFcfa(montant: number): string {
    return `${montant.toLocaleString('fr-FR')} F`;
  }

  selectTaille(taille: string): void {
    this.selectedTaille.set(taille);
    this.comboUnavailable.set(false);
  }

  selectCouleur(libelle: string): void {
    this.selectedCouleur.set(libelle);
    this.comboUnavailable.set(false);
  }

  /**
   * Ajout au panier : bloqué si le produit est épuisé, tant que taille et
   * couleur ne sont pas sélectionnées (voir canAddToCart), et si la
   * combinaison choisie ne correspond à aucune VarianteProduit réelle (ex :
   * "S" existe en noir mais pas en blanc) — auquel cas comboUnavailable
   * s'affiche à la place.
   */
  addToCart(): void {
    if (!this.canAddToCart()) {
      return;
    }
    const p = this.produit();
    if (!p) {
      return;
    }

    const taille = this.selectedTaille() ?? '—';
    const couleur = this.selectedCouleur() ?? '—';
    const varianteId = this.varianteIdByCombo.get(this.comboKey(taille, couleur));

    if (varianteId == null) {
      this.comboUnavailable.set(true);
      return;
    }

    this.cart.addLine({
      produitId: p.id,
      varianteId,
      nom: p.nom ?? '',
      imageUrl: this.imageUrl(),
      prix: Number(p.prix ?? 0),
      taille,
      couleur,
    });

    this.comboUnavailable.set(false);
    this.justAdded.set(true);
    this.cart.openDrawer();
    setTimeout(() => this.justAdded.set(false), 2000);
  }

  private comboKey(taille: string, couleur: string): string {
    return `${taille}${COMBO_SEPARATOR}${couleur}`;
  }

  private loadProduit(id: number): void {
    this.isLoading.set(true);
    this.produitService.find(id).subscribe(p => {
      this.produit.set(p);
      this.isLoading.set(false);
      if (!p) {
        return;
      }

      this.splitDescription(p.description ?? '');
      this.silhouette.set(SILHOUETTES[p.id % SILHOUETTES.length]);

      // NB : `categorie` est typé Pick<ICategorie, 'id'> côté généré ; on va
      // chercher le libellé séparément pour le fil d'Ariane.
      const catId = (p.categorie as { id?: number } | undefined)?.id ?? null;
      this.categorieId.set(catId);
      if (catId != null) {
        this.categorieService.find(catId).subscribe(cat => {
          this.categorieNom.set(cat.nom ?? '');
        });
        this.loadSuggestions(catId, id);
      }

      this.loadImage(id);
      this.loadVariantes(id);
    });
  }

  /** Découpe la description en accroche (1re ligne) + puces (lignes suivantes). */
  private splitDescription(description: string): void {
    const lignes = description
      .split('\n')
      .map(l => l.trim())
      .filter(l => l.length > 0);

    this.descriptionCourte.set(lignes[0] ?? '');
    this.detailsBullets.set(lignes.slice(1).map(l => l.replace(/^[-•]\s*/, '')));
  }

  private loadImage(id: number): void {
    const params = new HttpParams().set('ids', id);
    this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params }).subscribe(extras => {
      const image = extras.find(e => e.produitId === id)?.imageUrl;
      this.imageUrl.set(image ?? null);
    });
  }

  /**
   * Charge les variantes (taille + couleur + id) du produit. Aucune n'est
   * présélectionnée : la cliente doit choisir explicitement, sinon le
   * message d'avertissement reste affiché (voir canAddToCart). On construit
   * aussi varianteIdByCombo pour retrouver le bon id de VarianteProduit au
   * moment de l'ajout au panier (nécessaire pour créer une vraie
   * LigneCommande à la commande). Le stock total sert à détecter un produit
   * épuisé (voir epuise).
   */
  private loadVariantes(id: number): void {
    this.varianteProduitService.query({ 'produitId.in': id, size: 500 }).subscribe(response => {
      const variantes: IVarianteProduit[] = response.body ?? [];

      const taillesSet = new Set<string>();
      const couleursMap = new Map<string, CouleurInfo>();
      const comboMap = new Map<string, number>();
      let total = 0;

      for (const v of variantes) {
        const stock = v.stock ?? 0;
        total += Math.max(stock, 0);
        if (stock <= 0) {
          continue;
        }

        // NB : mêmes contournements de typage que dans shop-home.ts / shop-catalog.ts —
        // le backend renvoie les objets Taille/Couleur complets.
        const libelleTaille = (v.taille as { libelle?: string } | undefined)?.libelle;
        const couleur = v.couleur as { libelle?: string; codeHex?: string } | undefined;

        if (libelleTaille) {
          taillesSet.add(libelleTaille);
        }
        if (couleur?.libelle) {
          couleursMap.set(couleur.libelle, {
            libelle: couleur.libelle,
            codeHex: couleur.codeHex ?? '#CCCCCC',
          });
        }
        if (libelleTaille && couleur?.libelle && v.id != null) {
          comboMap.set(this.comboKey(libelleTaille, couleur.libelle), v.id);
        }
      }

      const taillesTriees = Array.from(taillesSet).sort((a, b) => TAILLE_ORDER.indexOf(a) - TAILLE_ORDER.indexOf(b));
      this.tailles.set(taillesTriees);

      const couleursListe = Array.from(couleursMap.values());
      this.couleurs.set(couleursListe);

      this.varianteIdByCombo = comboMap;
      this.stockTotal.set(total);
      this.variantesLoaded.set(true);
    });
  }

  private loadNouveauteStatus(id: number): void {
    this.produitService.query({ sort: ['id,desc'], size: NOUVEAUTE_LIMIT }).subscribe(response => {
      const ids = (response.body ?? []).map(p => p.id);
      this.isNouveau.set(ids.includes(id));
    });
  }

  /** Charge quelques produits de la même catégorie (hors produit courant) pour "Vous aimerez aussi". */
  private loadSuggestions(categorieId: number, excludeId: number): void {
    this.produitService
      .query({
        'categorieId.in': categorieId,
        'id.notEquals': excludeId,
        sort: ['id,desc'],
        size: SUGGESTIONS_LIMIT,
      })
      .subscribe(response => {
        const produits: IProduit[] = response.body ?? [];
        const items: ProduitSuggestion[] = produits.map((p, i) => ({
          id: p.id,
          nom: p.nom ?? '',
          prix: Number(p.prix ?? 0),
          tone: TONES[i % TONES.length],
          silhouette: SILHOUETTES[i % SILHOUETTES.length],
          couleurs: [],
          epuise: false,
        }));
        this.suggestions.set(items);

        if (items.length === 0) {
          return;
        }
        const ids = items.map(p => p.id);
        this.loadSuggestionImages(ids);
        this.loadSuggestionCouleurs(ids);
      });
  }

  private loadSuggestionImages(ids: number[]): void {
    let params = new HttpParams();
    ids.forEach(id => (params = params.append('ids', id)));

    this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params }).subscribe(extras => {
      const imageById = new Map(extras.filter(e => e.imageUrl).map(e => [e.produitId, e.imageUrl as string]));
      this.suggestions.update(list => list.map(item => (imageById.has(item.id) ? { ...item, imageUrl: imageById.get(item.id) } : item)));
    });
  }

  private loadSuggestionCouleurs(ids: number[]): void {
    this.varianteProduitService.query({ 'produitId.in': ids, size: 500 }).subscribe(response => {
      const variantes: IVarianteProduit[] = response.body ?? [];
      const couleursByProduit = new Map<number, Map<string, CouleurInfo>>();
      const stockByProduit = new Map<number, number>();

      for (const v of variantes) {
        const produitId = v.produit?.id;
        if (produitId == null) {
          continue;
        }
        const stock = v.stock ?? 0;
        stockByProduit.set(produitId, (stockByProduit.get(produitId) ?? 0) + Math.max(stock, 0));
        if (stock <= 0) {
          continue;
        }
        const couleur = v.couleur as { libelle?: string; codeHex?: string } | undefined;
        if (couleur?.libelle) {
          if (!couleursByProduit.has(produitId)) {
            couleursByProduit.set(produitId, new Map());
          }
          couleursByProduit.get(produitId)!.set(couleur.libelle, {
            libelle: couleur.libelle,
            codeHex: couleur.codeHex ?? '#CCCCCC',
          });
        }
      }

      this.suggestions.update(list =>
        list.map(item => ({
          ...item,
          couleurs: Array.from((couleursByProduit.get(item.id) ?? new Map()).values()),
          epuise: (stockByProduit.get(item.id) ?? 0) === 0,
        })),
      );
    });
  }
}
