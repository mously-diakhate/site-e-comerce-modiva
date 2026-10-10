import { NgClass } from '@angular/common';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';
import { ICategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
// Adaptez ce chemin si votre dossier généré diffère.
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';

type Silhouette = 'blazer' | 'chemise' | 'robe' | 'ensemble';

interface CategorieHome {
  key: string;
  label: string;
  tagline: string;
  queryValue: string;
  tone: string; // classe CSS 'cat-1' | 'cat-2' | 'cat-3' | 'cat-4'
  silhouette: Silhouette;
  imageUrl?: string;
  /** Résolu depuis le backend (voir loadCategories) — id réel de la catégorie en base. */
  categorieId?: number;
}

interface NouveauteHome {
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
const TAILLE_ORDER = ['XS', 'S', 'M', 'L', 'XL', '2XL', '3XL'];

@Component({
  selector: 'jhi-shop-home',
  templateUrl: './shop-home.html',
  styleUrl: './shop-home.scss',
  imports: [RouterLink, FontAwesomeModule, NgClass],
})
export default class ShopHome implements OnInit {
  private readonly accountService = inject(AccountService);
  private readonly produitService = inject(ProduitService);
  private readonly categorieService = inject(CategorieService);
  private readonly varianteProduitService = inject(VarianteProduitService);
  // Utilisé uniquement pour l'endpoint sans service généré dédié :
  // /api/produits/list-extras (images).
  private readonly http = inject(HttpClient);

  readonly account = this.accountService.account;
  readonly currentYear = new Date().getFullYear();

  // ---------------------------------------------------------------------
  // Images réelles de marque (hero, section "La femme MODIVA") — laissez
  // vide tant que vous n'avez pas déposé vos photos dans
  // src/main/webapp/content/images/home/. L'illustration SVG s'affiche
  // à la place tant que c'est vide.
  // ---------------------------------------------------------------------
  readonly heroImageUrl = 'content/images/modiva-hero.jpg';
  readonly femmeImages: string[] = ['content/images/femme-1.jpg', 'content/images/femme-2.jpg', 'content/images/femme-3.jpg'];

  /** Produits réels, chargés depuis le backend (voir loadNouveautes). */
  readonly nouveautes = signal<NouveauteHome[]>([]);
  /** produitId -> libellés de tailles disponibles (avec du stock), triés XS→3XL. */
  readonly productTailles = signal<Record<number, string[]>>({});
  /** produitId -> couleurs disponibles (avec du stock), libellé + code hexa. */
  readonly productCouleurs = signal<Record<number, CouleurInfo[]>>({});
  /** produitId -> true si le stock total (toutes variantes) est à 0. */
  readonly productEpuise = signal<Record<number, boolean>>({});

  /**
   * Vitrine de catégories : labels/taglines/illustrations pensés pour la
   * marque. categorieId et imageUrl sont résolus dynamiquement depuis le
   * backend (voir loadCategories / loadCategoryCoverImage) : imageUrl
   * devient la première photo d'un produit réel de cette catégorie, dès
   * qu'il y en a une. Tant qu'il n'y en a pas, la silhouette SVG reste
   * affichée.
   */
  readonly categories = signal<CategorieHome[]>([
    {
      key: 'bureau',
      label: 'Bureau',
      tagline: 'Blazers et tailleurs structurés',
      queryValue: 'bureau',
      tone: 'cat-1',
      silhouette: 'blazer',
    },
    {
      key: 'quotidien',
      label: 'Quotidien',
      tagline: 'Chemises et pièces faciles à porter',
      queryValue: 'quotidien',
      tone: 'cat-2',
      silhouette: 'chemise',
    },
    {
      key: 'robes',
      label: 'Robes',
      tagline: 'Silhouettes fluides pour vos soirées',
      queryValue: 'robes',
      tone: 'cat-3',
      silhouette: 'robe',
    },
    {
      key: 'ensembles',
      label: 'Ensembles',
      tagline: 'Duos coordonnés, prêts à porter',
      queryValue: 'ensembles',
      tone: 'cat-4',
      silhouette: 'ensemble',
    },
  ]);

  ngOnInit(): void {
    this.loadNouveautes();
    this.loadCategories();
  }

  formatFcfa(montant: number): string {
    return `${montant.toLocaleString('fr-FR')} F`;
  }

  /**
   * Récupère les 4 derniers produits via le vrai ProduitService (query),
   * puis leur première image via api/produits/list-extras, et leurs
   * variantes (tailles + couleurs + stock) via VarianteProduitService.
   */
  private loadNouveautes(): void {
    const req: any = { sort: ['id,desc'], size: 4, eagerload: true };
    // Si le statut est filtrable côté backend (ex: uniquement les produits
    // ACTIF), décommentez la ligne suivante :
    // req['statut.equals'] = 'ACTIF';

    this.produitService.query(req).subscribe(response => {
      const produits: IProduit[] = response.body ?? [];
      const items: NouveauteHome[] = produits.map((p, i) => ({
        id: p.id,
        nom: p.nom ?? '',
        prix: Number(p.prix ?? 0),
        tone: TONES[i % TONES.length],
        silhouette: SILHOUETTES[i % SILHOUETTES.length],
      }));
      this.nouveautes.set(items);

      if (produits.length === 0) {
        return;
      }

      const ids = produits.map(p => p.id);

      let extraParams = new HttpParams();
      ids.forEach(id => (extraParams = extraParams.append('ids', id)));

      this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params: extraParams }).subscribe(extras => {
        const imageById = new Map(extras.filter(e => e.imageUrl).map(e => [e.produitId, e.imageUrl as string]));
        this.nouveautes.update(list => list.map(item => (imageById.has(item.id) ? { ...item, imageUrl: imageById.get(item.id) } : item)));
      });

      this.loadVariantes(ids);
    });
  }

  /**
   * Récupère en un seul appel les variantes (taille + couleur + stock) de
   * tous les produits affichés, puis calcule pour chacun : la liste des
   * tailles avec du stock, la liste des couleurs avec du stock, et si le
   * produit est totalement épuisé.
   * `size: 500` : sans cela, la requête s'arrête à 20 variantes par défaut
   * et certains produits passeraient à tort pour "épuisés".
   */
  private loadVariantes(produitIds: number[]): void {
    this.varianteProduitService.query({ 'produitId.in': produitIds, size: 500 }).subscribe(response => {
      const variantes: IVarianteProduit[] = response.body ?? [];

      const taillesByProduit = new Map<number, Set<string>>();
      const couleursByProduit = new Map<number, Map<string, CouleurInfo>>();
      const stockByProduit = new Map<number, number>();

      for (const v of variantes) {
        const produitId = v.produit?.id;
        if (produitId == null) {
          continue;
        }
        const stock = v.stock ?? 0;
        stockByProduit.set(produitId, (stockByProduit.get(produitId) ?? 0) + Math.max(stock, 0));

        // NB : `taille` est typé Pick<ITaille, 'id'> côté généré, mais le
        // backend renvoie l'objet complet — même contournement que pour
        // les variantes dans le formulaire produit admin.
        const libelleTaille = (v.taille as { libelle?: string } | undefined)?.libelle;
        if (libelleTaille && stock > 0) {
          if (!taillesByProduit.has(produitId)) {
            taillesByProduit.set(produitId, new Set());
          }
          taillesByProduit.get(produitId)!.add(libelleTaille);
        }

        // Même contournement que pour `taille` : le backend renvoie
        // l'objet Couleur complet (libelle + codeHex), pas juste l'id.
        const couleur = v.couleur as { libelle?: string; codeHex?: string } | undefined;
        if (couleur?.libelle && stock > 0) {
          if (!couleursByProduit.has(produitId)) {
            couleursByProduit.set(produitId, new Map());
          }
          couleursByProduit.get(produitId)!.set(couleur.libelle, {
            libelle: couleur.libelle,
            codeHex: couleur.codeHex ?? '#CCCCCC',
          });
        }
      }

      const taillesRecord: Record<number, string[]> = {};
      taillesByProduit.forEach((set, produitId) => {
        taillesRecord[produitId] = Array.from(set).sort((a, b) => TAILLE_ORDER.indexOf(a) - TAILLE_ORDER.indexOf(b));
      });
      this.productTailles.set(taillesRecord);

      const couleursRecord: Record<number, CouleurInfo[]> = {};
      couleursByProduit.forEach((map, produitId) => {
        couleursRecord[produitId] = Array.from(map.values());
      });
      this.productCouleurs.set(couleursRecord);

      const epuiseRecord: Record<number, boolean> = {};
      produitIds.forEach(id => {
        epuiseRecord[id] = (stockByProduit.get(id) ?? 0) === 0;
      });
      this.productEpuise.set(epuiseRecord);
    });
  }

  /**
   * Résout l'id réel de chaque catégorie en base (comparaison par nom),
   * puis déclenche la récupération de sa photo de couverture dynamique.
   */
  private loadCategories(): void {
    this.categorieService.query({ size: 100 }).subscribe(response => {
      const backendCategories: ICategorie[] = response.body ?? [];
      const resolved = this.categories().map(cat => {
        const match = backendCategories.find(c => (c.nom ?? '').toLowerCase() === cat.label.toLowerCase());
        return match ? { ...cat, categorieId: match.id } : cat;
      });
      this.categories.set(resolved);

      resolved.forEach(cat => {
        if (cat.categorieId != null && !cat.imageUrl) {
          this.loadCategoryCoverImage(cat.key, cat.categorieId);
        }
      });
    });
  }

  /**
   * Va chercher le produit le plus récent de la catégorie donnée (via
   * ProduitService.query avec le filtre categorieId.in), puis sa première
   * image, et met à jour la vignette correspondante dès qu'elle est
   * disponible.
   */
  private loadCategoryCoverImage(key: string, categorieId: number): void {
    const req: any = { 'categorieId.in': categorieId, sort: ['id,desc'], size: 1 };

    this.produitService.query(req).subscribe(response => {
      const produits: IProduit[] = response.body ?? [];
      if (produits.length === 0) {
        return;
      }
      const produitId = produits[0].id;
      const extraParams = new HttpParams().set('ids', produitId);

      this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params: extraParams }).subscribe(extras => {
        const image = extras.find(e => e.produitId === produitId)?.imageUrl;
        if (image) {
          this.categories.update(list => list.map(c => (c.key === key ? { ...c, imageUrl: image } : c)));
        }
      });
    });
  }
}
