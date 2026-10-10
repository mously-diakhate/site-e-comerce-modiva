import { NgClass } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';

import { TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { ICategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
// Adaptez ce chemin si votre dossier généré diffère (voir shop-home.ts).
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { ItemCount } from 'app/shared/pagination';

type Silhouette = 'blazer' | 'chemise' | 'robe' | 'ensemble';

interface ProduitCatalogue {
  id: number;
  nom: string;
  prix: number;
  tone: string; // classe CSS 'p-tone-1' | 'p-tone-2' | 'p-tone-3' | 'p-tone-4'
  silhouette: Silhouette;
  imageUrl?: string;
}

interface ProduitListExtra {
  produitId: number;
  imageUrl: string | null;
}

/** Couleur disponible pour un produit (libellé + code hexa pour la pastille). */
interface CouleurInfo {
  libelle: string;
  codeHex: string;
}

const TONES = ['p-tone-1', 'p-tone-2', 'p-tone-3', 'p-tone-4'];
const SILHOUETTES: Silhouette[] = ['blazer', 'chemise', 'robe', 'ensemble'];
const ITEMS_PER_PAGE = 12;
/** Nombre de derniers produits considérés comme "Nouveau" — identique aux 4 produits affichés dans la section Nouveautés de shop-home. */
const NOUVEAUTE_LIMIT = 4;

/**
 * Page publique "Toute la collection" (alias "Boutique") : bandeau titre +
 * recherche, filtre par catégorie (pilules), compteur d'articles, puis
 * grille de produits (nom, prix, couleurs, badge "Nouveau" sur les derniers
 * ajoutés, badge "Épuisé" quand tout le stock est à 0) avec pagination.
 * Réutilise les classes de shop-home pour un rendu visuel cohérent.
 */
@Component({
  selector: 'jhi-shop-catalog',
  templateUrl: './shop-catalog.html',
  styleUrl: './shop-catalog.scss',
  imports: [RouterLink, FormsModule, FontAwesomeModule, NgClass, NgbPagination, ItemCount],
})
export default class ShopCatalog implements OnInit {
  private readonly produitService = inject(ProduitService);
  private readonly categorieService = inject(CategorieService);
  private readonly varianteProduitService = inject(VarianteProduitService);
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly produits = signal<ProduitCatalogue[]>([]);
  /** produitId -> couleurs disponibles (avec du stock), libellé + code hexa. */
  readonly productCouleurs = signal<Record<number, CouleurInfo[]>>({});
  /** produitId -> true si le stock total (toutes variantes) est à 0. */
  readonly productEpuise = signal<Record<number, boolean>>({});
  /** Ids des derniers produits ajoutés — sert à afficher le badge "Nouveau". */
  readonly nouveauteIds = signal<Set<number>>(new Set());

  /** Catégories réelles chargées depuis le backend, pour les pilules de filtre. */
  readonly categories = signal<ICategorie[]>([]);
  readonly selectedCategorieId = signal<number | null>(null);

  /** Texte tapé dans le champ de recherche (lié via ngModel). */
  searchInput = '';
  /** Terme de recherche réellement appliqué à la requête (après clic/Entrée). */
  readonly searchTerm = signal('');

  readonly page = signal(1);
  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly sortValue = signal('id,desc');
  readonly isLoading = signal(false);

  ngOnInit(): void {
    this.loadCategories();
    this.loadNouveauteIds();
    // Écoute ?categorieId=... (sous-menu navbar / selectCategorie) et
    // ?q=... (barre de recherche de la navbar), et recharge à chaque
    // changement, y compris quand on est déjà sur cette page.
    this.route.queryParamMap.subscribe(params => {
      const catId = params.get('categorieId');
      this.selectedCategorieId.set(catId ? Number(catId) : null);

      const q = params.get('q') ?? '';
      this.searchInput = q;
      this.searchTerm.set(q);

      this.page.set(1);
      this.load();
    });
  }

  formatFcfa(montant: number): string {
    return `${montant.toLocaleString('fr-FR')} F`;
  }

  isNouveau(produitId: number): boolean {
    return this.nouveauteIds().has(produitId);
  }

  /** Lance la recherche par nom (bouton "Chercher" ou touche Entrée). */
  onSearch(): void {
    this.searchTerm.set(this.searchInput.trim());
    this.page.set(1);
    this.load();
  }

  /** Sélectionne une catégorie via les pilules (null = "Tout"), en le reflétant dans l'URL. */
  selectCategorie(categorieId: number | null): void {
    if (this.selectedCategorieId() === categorieId) {
      return;
    }
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { categorieId },
      queryParamsHandling: 'merge',
    });
  }

  onPageChange(page: number): void {
    this.page.set(page);
    // Remonte en haut de la grille pour que la nouvelle page soit visible.
    document.querySelector('.shop-catalog')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    this.load();
  }

  private loadCategories(): void {
    this.categorieService.query({ size: 100, sort: ['nom,asc'] }).subscribe(response => {
      this.categories.set(response.body ?? []);
    });
  }

  /**
   * Récupère les ids des N derniers produits ajoutés (indépendamment du
   * tri/filtre/page affichés) pour savoir sur quelles cartes afficher le
   * badge "Nouveau". Même critère que nouveautes() dans shop-home.ts.
   */
  private loadNouveauteIds(): void {
    this.produitService.query({ sort: ['id,desc'], size: NOUVEAUTE_LIMIT }).subscribe(response => {
      const ids = (response.body ?? []).map(p => p.id).filter((id): id is number => id != null);
      this.nouveauteIds.set(new Set(ids));
    });
  }

  /** Charge la page courante de produits, puis leurs images et variantes. */
  private load(): void {
    this.isLoading.set(true);
    const req: any = {
      page: this.page() - 1,
      size: this.itemsPerPage(),
      sort: [this.sortValue()],
      eagerload: true,
    };

    const categorieId = this.selectedCategorieId();
    if (categorieId != null) {
      req['categorieId.in'] = categorieId;
    }

    const term = this.searchTerm();
    if (term) {
      req['nom.contains'] = term;
    }

    this.produitService.query(req).subscribe({
      next: response => {
        const body: IProduit[] = response.body ?? [];
        const totalCount = response.headers.get(TOTAL_COUNT_RESPONSE_HEADER);
        this.totalItems.set(totalCount ? Number(totalCount) : 0);

        const items: ProduitCatalogue[] = body.map((p, i) => ({
          id: p.id,
          nom: p.nom ?? '',
          prix: Number(p.prix ?? 0),
          tone: TONES[i % TONES.length],
          silhouette: SILHOUETTES[i % SILHOUETTES.length],
        }));
        this.produits.set(items);
        this.isLoading.set(false);

        if (items.length === 0) {
          this.productCouleurs.set({});
          this.productEpuise.set({});
          return;
        }
        const ids = items.map(p => p.id);
        this.loadImages(ids);
        this.loadVariantes(ids);
      },
      error: () => this.isLoading.set(false),
    });
  }

  private loadImages(ids: number[]): void {
    let params = new HttpParams();
    ids.forEach(id => (params = params.append('ids', id)));

    this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params }).subscribe(extras => {
      const imageById = new Map(extras.filter(e => e.imageUrl).map(e => [e.produitId, e.imageUrl as string]));
      this.produits.update(list => list.map(item => (imageById.has(item.id) ? { ...item, imageUrl: imageById.get(item.id) } : item)));
    });
  }

  /**
   * Récupère en un seul appel les variantes (taille + couleur + stock) des
   * produits affichés, puis calcule pour chacun la liste des couleurs
   * réellement disponibles (stock > 0) et s'il est totalement épuisé.
   * `size: 500` : sans cela, la requête s'arrête à 20 variantes par défaut
   * et certains produits passeraient à tort pour "épuisés".
   */
  private loadVariantes(ids: number[]): void {
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

        // NB : `couleur` est typé Pick<..., 'id'> côté généré, mais le
        // backend renvoie l'objet complet — même contournement que dans
        // shop-home.ts.
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

      const couleursRecord: Record<number, CouleurInfo[]> = {};
      couleursByProduit.forEach((map, produitId) => {
        couleursRecord[produitId] = Array.from(map.values());
      });
      this.productCouleurs.set(couleursRecord);

      const epuiseRecord: Record<number, boolean> = {};
      ids.forEach(id => {
        epuiseRecord[id] = (stockByProduit.get(id) ?? 0) === 0;
      });
      this.productEpuise.set(epuiseRecord);
    });
  }
}
