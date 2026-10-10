import { inject } from '@angular/core';
import { Routes } from '@angular/router';

import { map } from 'rxjs';

import { AccountService } from 'app/core/auth/account.service';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

import { errorRoute } from './layouts/error/error.route';

/**
 * La page publique d'un produit (vue boutique) et la fiche produit de
 * l'espace admin partagent la même adresse : /produit/:id/view.
 * Cette garde laisse passer la route publique uniquement pour les non-admin ;
 * pour une administratrice, le routeur continue vers la route des entités
 * (ProduitDetail : stock, catégorie, promotions...).
 */
const notAdmin = () => {
  const accountService = inject(AccountService);
  return accountService.identity().pipe(map(() => !accountService.hasAnyAuthority([Authority.ADMIN])));
};

const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./home/home'),
    title: 'home.title',
  },
  {
    path: '',
    loadComponent: () => import('./layouts/navbar/navbar'),
    outlet: 'navbar',
  },
  {
    path: 'collection',
    loadComponent: () => import('./home/shop-catalog/shop-catalog'),
    title: 'Notre collection',
  },
  {
    path: 'notre-univers',
    loadComponent: () => import('./home/notre-univers/notre-univers'),
    title: 'Notre univers',
  },
  {
    path: 'contact',
    loadComponent: () => import('./home/contact/contact'),
    title: 'Contact',
  },
  {
    // Page produit publique (visiteurs et clientes uniquement) : pour une
    // administratrice, cette route est ignorée (voir notAdmin) et c'est la
    // fiche produit de l'espace admin qui s'affiche.
    path: 'produit/:id/view',
    loadComponent: () => import('./home/produit-vue/produit-vue'),
    canMatch: [notAdmin],
    title: 'Produit',
  },
  {
    path: 'panier',
    loadComponent: () => import('./home/panier-page/panier-page'),
    title: 'Votre panier',
  },
  {
    // Page cliente "Mes commandes" — lecture seule, réservée aux comptes
    // connectés (peu importe le rôle : une simple cliente doit pouvoir y
    // accéder). Ne pas confondre avec /commande, qui est l'espace CRUD
    // admin protégé par ROLE_ADMIN (voir entities/entity.routes.ts).
    path: 'mes-commandes',
    loadComponent: () => import('./home/mes-commandes/mes-commandes'),
    canActivate: [UserRouteAccessService],
    data: { authorities: [Authority.USER, Authority.ADMIN] },
    title: 'Mes commandes',
  },
  {
    path: 'admin',
    data: {
      authorities: [Authority.ADMIN],
    },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/admin.routes'),
  },
  {
    path: 'account',
    loadChildren: () => import('./account/account.route'),
  },
  {
    path: 'login',
    loadComponent: () => import('./login/login'),
    title: 'login.title',
  },
  {
    path: '',
    loadChildren: () => import('./entities/entity.routes'),
  },
  ...errorRoute,
];

export default routes;
