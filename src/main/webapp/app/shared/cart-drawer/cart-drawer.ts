import { DOCUMENT } from '@angular/common';
import { Component, HostListener, effect, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { CartService } from 'app/core/cart/cart.service';

@Component({
  selector: 'jhi-cart-drawer',
  standalone: true,
  imports: [RouterLink, FontAwesomeModule],
  templateUrl: './cart-drawer.html',
  styleUrl: './cart-drawer.scss',
})
export default class CartDrawer {
  readonly cart = inject(CartService);
  private readonly document = inject(DOCUMENT);

  constructor() {
    // Tiroir ouvert : on bloque le défilement de la page derrière (sinon, sur
    // mobile, le doigt fait défiler la page au lieu de la liste du panier).
    // Le nettoyage restaure le défilement à la fermeture.
    effect(onCleanup => {
      if (this.cart.isDrawerOpen()) {
        const body = this.document.body;
        const previous = body.style.overflow;
        body.style.overflow = 'hidden';
        onCleanup(() => {
          body.style.overflow = previous;
        });
      }
    });
  }

  /** Touche Échap : ferme le tiroir (accessibilité clavier). */
  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.cart.isDrawerOpen()) {
      this.cart.closeDrawer();
    }
  }
}
