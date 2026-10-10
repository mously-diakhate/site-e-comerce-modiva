import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { CartService } from 'app/core/cart/cart.service';

type ModePaiement = 'WAVE' | 'ORANGE_MONEY';
type Etape = 'panier' | 'infos';

interface PaiementOption {
  value: ModePaiement;
  label: string;
  /** Chemin vers le logo, à déposer dans src/main/webapp/content/images/paiement/. */
  iconUrl: string;
}

/** Réponse de POST /api/checkout. */
interface CheckoutResult {
  commandeId: number;
  numero: string;
  montantTotal: number;
}

const MESSAGE_ERREUR_PAR_DEFAUT = "Une erreur est survenue pendant l'envoi de votre commande. Merci de réessayer dans un instant.";

/**
 * Page panier / commande (route "panier") : liste des articles du panier
 * (CartService), puis formulaire "Vos informations" (coordonnées, mode de
 * paiement, livraison) en deuxième étape.
 *
 * À la validation, UN SEUL appel est fait : POST /api/checkout. Le navigateur
 * n'envoie que les coordonnées et, pour chaque article, { varianteId, quantite }.
 * C'est le serveur qui :
 *  - relit les prix en base (aucun prix ni total n'est envoyé : impossible de
 *    commander à 1 F en modifiant la requête) ;
 *  - vérifie et déduit le stock ;
 *  - crée ClienteProfile (invité) / Livraison / Paiement / Commande /
 *    LigneCommande dans une seule transaction ;
 *  - vide le panier serveur de la cliente connectée.
 * Fonctionne pareil pour un visiteur sans compte et pour une cliente connectée.
 */
@Component({
  selector: 'jhi-panier-page',
  templateUrl: './panier-page.html',
  styleUrl: './panier-page.scss',
  imports: [RouterLink, FormsModule, FontAwesomeModule],
})
export default class PanierPage {
  protected readonly cart = inject(CartService);
  private readonly http = inject(HttpClient);

  readonly paiementOptions: PaiementOption[] = [
    { value: 'WAVE', label: 'Wave', iconUrl: 'content/images/paiement/wave.png' },
    { value: 'ORANGE_MONEY', label: 'Orange Money', iconUrl: 'content/images/paiement/orange-money.png' },
  ];

  /** Étape courante : la liste des articles, puis le formulaire de coordonnées. */
  readonly etape = signal<Etape>('panier');

  nomComplet = '';
  telephone = '';
  pays = 'Sénégal';
  region = '';
  quartier = '';
  message = '';
  readonly modePaiement = signal<ModePaiement | null>(null);

  readonly showValidationError = signal(false);
  readonly orderPlaced = signal(false);
  readonly isSubmitting = signal(false);
  /** Affiché si la création de commande échoue côté serveur. */
  readonly submissionError = signal(false);
  /** Message affiché quand submissionError() est vrai (ex : "Stock insuffisant pour ..."). */
  readonly submissionErrorDetail = signal(MESSAGE_ERREUR_PAR_DEFAUT);

  readonly canPasserCommande = computed(
    () =>
      this.cart.items().length > 0 &&
      this.nomComplet.trim().length > 0 &&
      this.telephone.trim().length > 0 &&
      this.pays.trim().length > 0 &&
      this.region.trim().length > 0 &&
      this.quartier.trim().length > 0 &&
      this.modePaiement() !== null,
  );

  /** Passe à l'étape "Vos informations" (bouton "Commander" du récapitulatif). */
  allerAuxInfos(): void {
    if (this.cart.items().length === 0) {
      return;
    }
    this.etape.set('infos');
    document.querySelector('.panier-page')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  /** Retour à la liste des articles (bouton "Retour au panier"). */
  retourAuPanier(): void {
    this.etape.set('panier');
    document.querySelector('.panier-page')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  selectPaiement(mode: ModePaiement): void {
    this.modePaiement.set(mode);
    this.showValidationError.set(false);
  }

  passerCommande(): void {
    if (!this.canPasserCommande() || this.isSubmitting()) {
      this.showValidationError.set(true);
      return;
    }

    this.showValidationError.set(false);
    this.submissionError.set(false);
    this.isSubmitting.set(true);

    const payload = {
      nomComplet: this.nomComplet.trim(),
      telephone: this.telephone.trim(),
      pays: this.pays.trim(),
      region: this.region.trim(),
      quartier: this.quartier.trim(),
      message: this.message.trim() || null,
      modePaiement: this.modePaiement(),
      // Uniquement l'identité de l'article et la quantité : le prix est relu en base côté serveur.
      lignes: this.cart.items().map(l => ({ varianteId: l.varianteId, quantite: l.quantite })),
    };

    this.http.post<CheckoutResult>('api/checkout', payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.orderPlaced.set(true);
        this.cart.clearAfterOrder();
      },
      error: (err: HttpErrorResponse) => {
        this.isSubmitting.set(false);
        // 400 / 409 (ex : stock insuffisant) : le serveur renvoie un message lisible.
        const detail = err.status === 400 || err.status === 409 ? (err.error?.detail ?? err.error?.message) : null;
        this.submissionErrorDetail.set(typeof detail === 'string' && detail.length > 0 ? detail : MESSAGE_ERREUR_PAR_DEFAUT);
        this.submissionError.set(true);
      },
    });
  }
}
