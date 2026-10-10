import { Component, computed, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AccountService } from 'app/core/auth/account.service';

interface SocialLink {
  label: string;
  url: string;
  icon: [string, string];
}

@Component({
  selector: 'jhi-footer',
  templateUrl: './footer.html',
  styleUrl: './footer.scss',
  imports: [RouterLink, FormsModule, FontAwesomeModule],
})
export default class Footer {
  private readonly accountService = inject(AccountService);
  private readonly router = inject(Router);

  /** Le footer boutique ne s'affiche pas dans l'espace administratrice. */
  readonly isAdmin = computed(() => this.accountService.hasAnyAuthority(['ROLE_ADMIN']));
  readonly currentYear = new Date().getFullYear();

  // TODO : remplacez 'modiva' par les vrais pseudos TikTok et Snapchat dès
  // que vous les avez — Instagram est déjà le bon (modiva144).
  readonly socialLinks: SocialLink[] = [
    { label: 'Instagram', url: 'https://instagram.com/modiva144', icon: ['fab', 'instagram'] },
    { label: 'TikTok', url: 'https://tiktok.com/@modiva144', icon: ['fab', 'tiktok'] },
    { label: 'Snapchat', url: 'https://snapchat.com/add/lymatecheikh', icon: ['fab', 'snapchat'] },
  ];

  newsletterEmail = '';

  subscribeNewsletter(): void {
    // TODO : brancher sur un endpoint d'inscription newsletter une fois disponible côté backend.
    this.newsletterEmail = '';
  }

  /**
   * Emmène vers la section "Nouveautés" de l'accueil. Si on est déjà sur
   * l'accueil, on scrolle directement (le routing Angular ne redéclenche
   * pas la navigation vers la même URL) ; sinon on navigue puis on précise
   * le fragment cible.
   */
  goToNouveautes(): void {
    if (this.router.url === '/' || this.router.url.startsWith('/#') || this.router.url.startsWith('/?')) {
      document.getElementById('nouveautes')?.scrollIntoView({ behavior: 'smooth', block: 'start' });
    } else {
      this.router.navigate(['/'], { fragment: 'nouveautes' });
    }
  }
}
