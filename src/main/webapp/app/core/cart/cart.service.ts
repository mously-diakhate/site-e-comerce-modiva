import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, computed, effect, inject, signal, untracked } from '@angular/core';

import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AccountService } from 'app/core/auth/account.service';

export interface CartLine {
  /** Identifiant de ligne = produit + taille + couleur (une combinaison = une ligne distincte). */
  id: string;
  produitId: number;
  /** Id de la VarianteProduit exacte (taille + couleur) — nécessaire pour créer une vraie LigneCommande côté backend. */
  varianteId: number;
  nom: string;
  imageUrl: string | null;
  prix: number;
  taille: string;
  couleur: string;
  quantite: number;
  /**
   * Id du LignePanier côté backend. Présent uniquement pour une cliente
   * connectée dont cette ligne est déjà synchronisée avec le serveur.
   * Absent pour le panier d'un visiteur non connecté (local uniquement).
   */
  ligneId?: number;
}

/**
 * Forme réelle renvoyée par GET /api/variante-produits/:id. Le modèle
 * TypeScript généré ne déclare que Pick<..., 'id'> pour taille/couleur/produit :
 * le backend renvoie l'objet complet pour taille et couleur, mais pour le
 * produit seulement son id (le nom et le prix sont donc relus via
 * GET /api/produits/:id, voir finishHydration).
 */
interface RawVarianteDetail {
  id: number;
  taille?: { id: number; libelle?: string } | null;
  couleur?: { id: number; libelle?: string; codeHex?: string } | null;
  produit?: { id: number } | null;
}

/** Forme renvoyée par GET /api/produits/:id (nom et prix actuels). */
interface RawProduit {
  id: number;
  nom?: string;
  prix?: number | string;
}

interface ProduitListExtra {
  produitId: number;
  imageUrl: string | null;
}

interface LignePanierDTO {
  id: number;
  quantite: number;
  variante?: { id: number } | null;
  panier?: { id: number } | null;
}

interface PanierDTO {
  id: number;
  dateCreation?: string | null;
}

const STORAGE_KEY = 'modiva-panier';
/** Login de la personne à qui appartient le panier stocké dans le navigateur. */
const OWNER_KEY = 'modiva-panier-owner';

/**
 * Panier cliente. Deux modes de fonctionnement :
 *
 * - Visiteur non connecté : panier tenu uniquement côté navigateur
 *   (localStorage). Aucun appel réseau. Il peut commander sans compte.
 *
 * - Cliente connectée : en plus du localStorage (cache d'affichage immédiat),
 *   chaque ligne est synchronisée avec le backend via les endpoints
 *   self-service GET /api/paniers/me, GET /api/ligne-paniers/mine et
 *   POST/PATCH/DELETE /api/mon-panier/lignes, pour que le panier survive à un
 *   changement de navigateur ou d'appareil.
 *
 * Règles pour éviter qu'un panier "fantôme" survive :
 *  - Le panier local est étiqueté avec le login de son propriétaire
 *    (OWNER_KEY). Si une autre personne se connecte sur ce navigateur, le
 *    panier de la précédente est jeté au lieu d'être fusionné.
 *  - Dès que la session se termine (déconnexion, token expiré...), le panier
 *    local est vidé.
 *  - Après une commande réussie, utiliser clearAfterOrder() (le serveur vide
 *    déjà le panier serveur pendant POST /api/checkout).
 *  - Un panier "invité" (aucun propriétaire) rempli avant la connexion est
 *    fusionné avec le panier serveur de la cliente.
 *
 * Tout échec réseau pendant la synchronisation est silencieux : le panier
 * reste utilisable en local, seule la persistance cross-device est dégradée.
 */
@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);
  private readonly accountService = inject(AccountService);

  private readonly lines = signal<CartLine[]>(this.readFromStorage());

  readonly items = this.lines.asReadonly();
  readonly count = computed(() => this.lines().reduce((sum, l) => sum + l.quantite, 0));
  readonly total = computed(() => this.lines().reduce((sum, l) => sum + l.quantite * l.prix, 0));

  /** Piloté par CartDrawer — voir ce composant pour l'affichage du tiroir. */
  readonly isDrawerOpen = signal(false);

  /** Id du Panier backend de la cliente connectée, résolu une fois par session. */
  private panierId: number | null = null;
  private wasLoggedIn = false;

  constructor() {
    effect(() => this.writeToStorage(this.lines()));

    // Détecte les transitions de session. `untracked` évite que les signaux
    // lus dans les méthodes appelées (this.lines()...) ne re-déclenchent
    // cet effet.
    effect(() => {
      const account = this.accountService.account();
      if (account && !this.wasLoggedIn) {
        this.wasLoggedIn = true;
        untracked(() => this.onLogin(account.login));
      } else if (!account && this.wasLoggedIn) {
        // Session terminée (déconnexion volontaire, token expiré...) :
        // on ne laisse aucun panier derrière nous dans ce navigateur.
        // Le `&& this.wasLoggedIn` évite de vider le panier d'un visiteur
        // au simple démarrage de l'appli, quand le compte n'est pas encore chargé.
        this.wasLoggedIn = false;
        untracked(() => {
          this.panierId = null;
          this.lines.set([]);
          this.removeOwner();
        });
      }
    });
  }

  openDrawer(): void {
    this.isDrawerOpen.set(true);
  }

  closeDrawer(): void {
    this.isDrawerOpen.set(false);
  }

  addLine(input: Omit<CartLine, 'id' | 'quantite' | 'ligneId'>, quantite = 1): void {
    if (!input.nom || !input.taille || !input.couleur || !input.varianteId || quantite <= 0) {
      // Sécurité : on n'ajoute jamais une ligne sans nom / taille / couleur /
      // varianteId valides, même si l'appelant a un bug.
      return;
    }
    const id = `${input.produitId}-${input.taille}-${input.couleur}`;
    let createdNew = false;
    let resultingQuantite = quantite;

    this.lines.update(list => {
      const existing = list.find(l => l.id === id);
      if (existing) {
        resultingQuantite = existing.quantite + quantite;
        return list.map(l => (l.id === id ? { ...l, quantite: resultingQuantite } : l));
      }
      createdNew = true;
      return [...list, { ...input, id, quantite }];
    });

    if (!this.accountService.account()) {
      return;
    }
    if (createdNew) {
      this.syncCreateLine(id, input.varianteId, quantite);
    } else {
      const line = this.lines().find(l => l.id === id);
      if (line?.ligneId) {
        this.syncUpdateLine(line.ligneId, resultingQuantite);
      }
    }
  }

  updateQuantite(id: string, quantite: number): void {
    if (quantite <= 0) {
      this.removeLine(id);
      return;
    }
    const line = this.lines().find(l => l.id === id);
    this.lines.update(list => list.map(l => (l.id === id ? { ...l, quantite } : l)));

    if (this.accountService.account() && line?.ligneId) {
      this.syncUpdateLine(line.ligneId, quantite);
    }
  }

  removeLine(id: string): void {
    const line = this.lines().find(l => l.id === id);
    this.lines.update(list => list.filter(l => l.id !== id));

    if (this.accountService.account() && line?.ligneId) {
      this.syncDeleteLine(line.ligneId);
    }
  }

  /**
   * Vide uniquement le panier local (utilisé à la déconnexion : on ne veut
   * PAS supprimer le panier serveur de la cliente, elle le retrouvera à sa
   * prochaine connexion).
   */
  clear(): void {
    this.lines.set([]);
    this.panierId = null;
  }

  /**
   * À appeler après une commande réussie. Le serveur vide lui-même le panier
   * de la cliente connectée pendant POST /api/checkout (les articles commandés
   * ne reviendront donc pas à la prochaine connexion) : il ne reste ici qu'à
   * vider l'affichage local.
   */
  clearAfterOrder(): void {
    this.lines.set([]);
  }

  formatFcfa(montant: number): string {
    return `${montant.toLocaleString('fr-FR')} F`;
  }

  // ---------------------------------------------------------------------
  // Gestion du propriétaire du panier local
  // ---------------------------------------------------------------------

  /**
   * Appelé à la connexion. Si le panier stocké dans ce navigateur appartenait
   * à une autre personne, on le jette. S'il n'a pas de propriétaire (panier
   * invité) ou appartient à la même personne, on le conserve et on le fusionne
   * avec le panier serveur.
   */
  private onLogin(login: string): void {
    let previousOwner: string | null = null;
    try {
      previousOwner = localStorage.getItem(OWNER_KEY);
    } catch {
      // Stockage indisponible : on continue sans propriétaire connu.
    }

    if (previousOwner && previousOwner !== login) {
      this.lines.set([]);
    }

    try {
      localStorage.setItem(OWNER_KEY, login);
    } catch {
      // ignoré
    }

    this.syncOnLogin();
  }

  private removeOwner(): void {
    try {
      localStorage.removeItem(OWNER_KEY);
    } catch {
      // ignoré
    }
  }

  // ---------------------------------------------------------------------
  // Synchronisation backend (cliente connectée uniquement)
  // ---------------------------------------------------------------------

  /**
   * Au moment de la connexion : récupère le panier serveur de la cliente
   * (créé côté serveur s'il n'existe pas), charge ses lignes existantes, les
   * hydrate avec les détails produit/variante, puis fusionne avec ce qu'il y
   * avait déjà en local (panier "invité" avant connexion).
   */
  private syncOnLogin(): void {
    this.http.get<PanierDTO>('api/paniers/me').subscribe({
      next: panier => {
        this.panierId = panier.id;
        this.http.get<LignePanierDTO[]>('api/ligne-paniers/mine').subscribe({
          next: serverLignes => this.hydrateAndMerge(serverLignes),
          error: () => {
            /* pas bloquant : le panier reste utilisable en local */
          },
        });
      },
      error: () => {
        // Échec réseau ou serveur : on reste en mode local.
      },
    });
  }

  private hydrateAndMerge(serverLignes: LignePanierDTO[]): void {
    if (serverLignes.length === 0) {
      // Rien côté serveur : on pousse les lignes locales existantes
      // (panier invité rempli avant la connexion).
      this.lines().forEach(line => {
        if (!line.ligneId) {
          this.syncCreateLine(line.id, line.varianteId, line.quantite);
        }
      });
      return;
    }

    const varianteIds = [...new Set(serverLignes.map(l => l.variante?.id).filter((v): v is number => v != null))];
    if (varianteIds.length === 0) {
      return;
    }

    const details = new Map<number, RawVarianteDetail>();
    let received = 0;
    varianteIds.forEach(varianteId => {
      this.http.get<RawVarianteDetail>(`api/variante-produits/${varianteId}`).subscribe({
        next: detail => {
          details.set(varianteId, detail);
          received++;
          if (received === varianteIds.length) {
            this.finishHydration(serverLignes, details);
          }
        },
        error: () => {
          received++;
          if (received === varianteIds.length) {
            this.finishHydration(serverLignes, details);
          }
        },
      });
    });
  }

  private finishHydration(serverLignes: LignePanierDTO[], details: Map<number, RawVarianteDetail>): void {
    const produitIds = [...new Set([...details.values()].map(d => d.produit?.id).filter((v): v is number => v != null))];

    const applyLines = (produitById: Map<number, RawProduit>, imageByProduit: Map<number, string>) => {
      const serverCartLines: CartLine[] = serverLignes
        .map(ligne => {
          const varianteId = ligne.variante?.id;
          if (varianteId == null) {
            return null;
          }
          const detail = details.get(varianteId);
          const produitId = detail?.produit?.id;
          const tailleLibelle = detail?.taille?.libelle ?? '';
          const couleurLibelle = detail?.couleur?.libelle ?? '';
          if (!produitId || !tailleLibelle || !couleurLibelle) {
            return null;
          }
          // Nom et prix viennent du vrai produit (la variante ne renvoie que son id).
          const produit = produitById.get(produitId);
          const nom = produit?.nom ?? '';
          if (!nom) {
            return null; // on n'affiche jamais une ligne sans nom ni prix fiables
          }
          const cartLine: CartLine = {
            id: `${produitId}-${tailleLibelle}-${couleurLibelle}`,
            produitId,
            varianteId,
            nom,
            imageUrl: imageByProduit.get(produitId) ?? null,
            prix: Number(produit?.prix ?? 0),
            taille: tailleLibelle,
            couleur: couleurLibelle,
            quantite: ligne.quantite,
            ligneId: ligne.id,
          };
          return cartLine;
        })
        .filter((l): l is CartLine => l !== null);

      // Fusion : les lignes serveur ont priorité (elles ont un ligneId).
      // Les lignes locales qui n'ont pas d'équivalent serveur (même id
      // composite) sont conservées et poussées vers le backend.
      const serverIds = new Set(serverCartLines.map(l => l.id));
      const localOnly = this.lines().filter(l => !serverIds.has(l.id) && !l.ligneId);

      this.lines.set([...serverCartLines, ...localOnly]);

      localOnly.forEach(line => this.syncCreateLine(line.id, line.varianteId, line.quantite));
    };

    if (produitIds.length === 0) {
      applyLines(new Map(), new Map());
      return;
    }

    let params = new HttpParams();
    produitIds.forEach(id => (params = params.append('ids', id)));

    forkJoin({
      produits: forkJoin(produitIds.map(id => this.http.get<RawProduit>(`api/produits/${id}`).pipe(catchError(() => of(null))))),
      extras: this.http
        .get<ProduitListExtra[]>('api/produits/list-extras', { params })
        .pipe(catchError(() => of([] as ProduitListExtra[]))),
    }).subscribe(({ produits, extras }) => {
      const produitById = new Map<number, RawProduit>();
      produits.forEach(p => {
        if (p) {
          produitById.set(p.id, p);
        }
      });
      const imageByProduit = new Map(extras.filter(e => e.imageUrl).map(e => [e.produitId, e.imageUrl as string]));
      applyLines(produitById, imageByProduit);
    });
  }

  private syncCreateLine(compositeId: string, varianteId: number, quantite: number): void {
    if (this.panierId == null) {
      return;
    }
    this.http.post<LignePanierDTO>('api/mon-panier/lignes', { varianteId, quantite }).subscribe({
      next: created => {
        this.lines.update(list => list.map(l => (l.id === compositeId ? { ...l, ligneId: created.id } : l)));
      },
      error: () => {
        /* échec silencieux : la ligne reste utilisable en local, sans ligneId */
      },
    });
  }

  private syncUpdateLine(ligneId: number, quantite: number): void {
    this.http.patch(`api/mon-panier/lignes/${ligneId}`, { quantite }).subscribe({
      error: () => {
        /* échec silencieux */
      },
    });
  }

  private syncDeleteLine(ligneId: number): void {
    this.http.delete(`api/mon-panier/lignes/${ligneId}`).subscribe({
      error: () => {
        /* échec silencieux */
      },
    });
  }

  // ---------------------------------------------------------------------
  // Persistance locale (localStorage) — sert de cache immédiat dans tous
  // les cas, et de seule source de vérité pour un visiteur non connecté.
  // ---------------------------------------------------------------------

  private readFromStorage(): CartLine[] {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      if (!raw) {
        return [];
      }
      const parsed: unknown = JSON.parse(raw);
      if (!Array.isArray(parsed)) {
        return [];
      }
      // On ne garde que les lignes complètes : une ligne sans nom, sans
      // taille/couleur/varianteId renseignés ou sans quantité valide ne doit
      // jamais s'afficher. ligneId est optionnel et n'est pas validé ici.
      return (parsed as Partial<CartLine>[]).filter(
        (l): l is CartLine =>
          typeof l.id === 'string' &&
          l.id.length > 0 &&
          typeof l.produitId === 'number' &&
          typeof l.varianteId === 'number' &&
          typeof l.nom === 'string' &&
          l.nom.length > 0 &&
          typeof l.prix === 'number' &&
          typeof l.taille === 'string' &&
          l.taille.length > 0 &&
          typeof l.couleur === 'string' &&
          l.couleur.length > 0 &&
          typeof l.quantite === 'number' &&
          l.quantite > 0,
      );
    } catch {
      return [];
    }
  }

  private writeToStorage(lines: CartLine[]): void {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(lines));
    } catch {
      // Stockage indisponible (navigation privée, quota atteint...) — on ignore silencieusement.
    }
  }
}
