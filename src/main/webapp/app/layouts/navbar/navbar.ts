import { Component, ElementRef, OnInit, computed, inject, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbCollapse } from '@ng-bootstrap/ng-bootstrap/collapse';
import { NgbDropdown, NgbDropdownMenu, NgbDropdownToggle } from '@ng-bootstrap/ng-bootstrap/dropdown';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { environment } from 'environments/environment';

import { LANGUAGES } from 'app/config/language.constants';
import { AccountService } from 'app/core/auth/account.service';
import { StateStorageService } from 'app/core/auth/state-storage.service';
import { CartService } from 'app/core/cart/cart.service';
import { ProfileService } from 'app/layouts/profiles/profile.service';
import { LoginService } from 'app/login/login.service';
import { ICategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
import CartDrawer from 'app/shared/cart-drawer/cart-drawer';

interface AdminNavItem {
  label: string;
  icon: string;
  link: string;
  exact?: boolean;
}

interface SubLink {
  label: string;
  link: string;
  icon?: string;
}

@Component({
  selector: 'jhi-navbar',
  templateUrl: './navbar.html',
  styleUrl: './navbar.scss',
  imports: [
    RouterLink,
    RouterLinkActive,
    FontAwesomeModule,
    NgbCollapse,
    NgbDropdown,
    NgbDropdownMenu,
    NgbDropdownToggle,
    TranslateModule,
    CartDrawer,
    FormsModule,
  ],
})
export default class Navbar implements OnInit {
  readonly inProduction = signal(true);
  readonly isNavbarCollapsed = signal(true);
  readonly isSidebarOpen = signal(false);
  readonly languages = LANGUAGES;
  readonly openAPIEnabled = signal(false);
  readonly version: string;
  readonly account = inject(AccountService).account;

  /** Catégories réelles, pour le sous-menu "Catégorie" de la navbar visiteur/cliente. */
  readonly categories = signal<ICategorie[]>([]);

  /** Panier : le template utilise `cart.count()` pour le badge. */
  protected readonly cart = inject(CartService);

  /** Barre de recherche navbar : ouverte/fermée + texte tapé. */
  readonly isSearchOpen = signal(false);
  searchQuery = '';
  private readonly navSearchInput = viewChild<ElementRef>('navSearchInput');

  private readonly accountService = inject(AccountService);
  private readonly loginService = inject(LoginService);
  private readonly translateService = inject(TranslateService);
  private readonly stateStorageService = inject(StateStorageService);
  private readonly profileService = inject(ProfileService);
  private readonly categorieService = inject(CategorieService);
  private readonly router = inject(Router);

  /** La sidebar "espace administratrice" ne s'affiche qu'aux comptes ROLE_ADMIN. */
  readonly isAdmin = computed(() => this.accountService.hasAnyAuthority(['ROLE_ADMIN']));

  /** Menu principal de la sidebar admin — reflète les modules métier du cahier des charges. */
  readonly adminNav: AdminNavItem[] = [
    { label: 'Tableau de bord', icon: 'chart-pie', link: '/', exact: true },
    { label: 'Produits', icon: 'bag-shopping', link: '/produit' },
    { label: 'Commandes', icon: 'box', link: '/commande' },
    { label: 'Clientes', icon: 'users', link: '/cliente-profile' },
    { label: 'Promotions', icon: 'tag', link: '/promotion' },
  ];

  /** Sous-liens regroupés derrière l'icône "Paramètres" — uniquement les référentiels catalogue. */
  readonly catalogLinks: SubLink[] = [
    { label: 'Catégories', link: '/categorie' },
    { label: 'Tailles', link: '/taille' },
    { label: 'Couleurs', link: '/couleur' },
  ];

  constructor() {
    const { VERSION } = environment;
    this.version = VERSION ? (VERSION.toLowerCase().startsWith('v') ? VERSION : `v${VERSION}`) : '';
  }

  ngOnInit(): void {
    this.profileService.getProfileInfo().subscribe(profileInfo => {
      this.inProduction.set(profileInfo.inProduction ?? true);
      this.openAPIEnabled.set(profileInfo.openAPIEnabled ?? false);
    });
    this.loadCategories();
  }

  /** Initiales affichées dans l'avatar de la sidebar (ex : "AD"). */
  initiales(): string {
    const acc = this.account();
    if (!acc) {
      return '';
    }
    const first = acc.firstName?.[0] ?? '';
    const last = acc.lastName?.[0] ?? '';
    return `${first}${last}`.toUpperCase() || acc.login.slice(0, 2).toUpperCase();
  }

  changeLanguage(languageKey: string): void {
    this.stateStorageService.storeLocale(languageKey);
    this.translateService.use(languageKey);
  }

  collapseNavbar(): void {
    this.isNavbarCollapsed.set(true);
  }

  toggleSidebar(): void {
    this.isSidebarOpen.update(open => !open);
  }

  closeSidebar(): void {
    this.isSidebarOpen.set(false);
  }

  login(): void {
    this.router.navigate(['/login']);
  }

  logout(): void {
    this.collapseNavbar();
    this.closeSidebar();
    // Le panier ne doit jamais survivre à une déconnexion : sinon la
    // prochaine personne à utiliser ce navigateur (compte partagé, poste
    // public...) verrait les articles de la précédente.
    this.cart.clear();
    this.loginService.logout();
    this.router.navigate(['']);
  }

  /**
   * Clic sur la loupe : ouvre le champ s'il est fermé (et lui donne le
   * focus) ; s'il est déjà ouvert et contient du texte, lance la
   * recherche ; s'il est ouvert et vide, le referme.
   */
  toggleSearch(): void {
    if (this.isSearchOpen()) {
      if (this.searchQuery.trim()) {
        this.submitSearch();
      } else {
        this.closeSearch();
      }
      return;
    }
    this.isSearchOpen.set(true);
    setTimeout(() => this.navSearchInput()?.nativeElement.focus());
  }

  /** Lance la recherche : redirige vers la boutique avec le terme en query param. */
  submitSearch(): void {
    const term = this.searchQuery.trim();
    if (!term) {
      return;
    }
    this.router.navigate(['/collection'], { queryParams: { q: term } });
    this.closeSearch();
  }

  closeSearch(): void {
    this.isSearchOpen.set(false);
    this.searchQuery = '';
  }

  /** Ferme le champ si on clique ailleurs sans avoir tapé de terme. */
  onSearchBlur(): void {
    setTimeout(() => {
      if (!this.searchQuery.trim()) {
        this.closeSearch();
      }
    }, 150);
  }

  /** Charge les catégories réelles pour le sous-menu "Catégorie" de la navbar. */
  private loadCategories(): void {
    this.categorieService.query({ size: 100, sort: ['nom,asc'] }).subscribe(response => {
      this.categories.set(response.body ?? []);
    });
  }
}
