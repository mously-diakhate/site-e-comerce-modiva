import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { Authority } from 'app/shared/jhipster/constants';

// ---------------------------------------------------------------------
// Toutes les routes ci-dessous sont l'espace CRUD généré par JHipster
// (créer / éditer / supprimer n'importe quelle entité). C'est l'espace
// administratrice : on le protège donc entièrement avec ROLE_ADMIN, pour
// qu'une cliente ne puisse plus y accéder même en tapant l'URL directement
// (ex: /commande, /commande/new, /commande/3/edit...).
//
// Les pages publiques équivalentes pour les clientes (boutique, mes
// commandes, etc.) sont déclarées séparément dans app.routes.ts.
// ---------------------------------------------------------------------
const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'modivaApp.adminAuthority.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'cliente-profile',
    data: { pageTitle: 'modivaApp.clienteProfile.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./cliente-profile/cliente-profile.routes'),
  },
  {
    path: 'adresse',
    data: { pageTitle: 'modivaApp.adresse.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./adresse/adresse.routes'),
  },
  {
    path: 'categorie',
    data: { pageTitle: 'modivaApp.categorie.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./categorie/categorie.routes'),
  },
  {
    path: 'produit',
    data: { pageTitle: 'modivaApp.produit.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./produit/produit.routes'),
  },
  {
    path: 'image-produit',
    data: { pageTitle: 'modivaApp.imageProduit.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./image-produit/image-produit.routes'),
  },
  {
    path: 'taille',
    data: { pageTitle: 'modivaApp.taille.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./taille/taille.routes'),
  },
  {
    path: 'couleur',
    data: { pageTitle: 'modivaApp.couleur.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./couleur/couleur.routes'),
  },
  {
    path: 'variante-produit',
    data: { pageTitle: 'modivaApp.varianteProduit.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./variante-produit/variante-produit.routes'),
  },
  {
    path: 'panier',
    data: { pageTitle: 'modivaApp.panier.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./panier/panier.routes'),
  },
  {
    path: 'ligne-panier',
    data: { pageTitle: 'modivaApp.lignePanier.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./ligne-panier/ligne-panier.routes'),
  },
  {
    path: 'commande',
    data: { pageTitle: 'modivaApp.commande.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./commande/commande.routes'),
  },
  {
    path: 'ligne-commande',
    data: { pageTitle: 'modivaApp.ligneCommande.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./ligne-commande/ligne-commande.routes'),
  },
  {
    path: 'paiement',
    data: { pageTitle: 'modivaApp.paiement.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./paiement/paiement.routes'),
  },
  {
    path: 'livraison',
    data: { pageTitle: 'modivaApp.livraison.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./livraison/livraison.routes'),
  },
  {
    path: 'promotion',
    data: { pageTitle: 'modivaApp.promotion.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./promotion/promotion.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title', authorities: [Authority.ADMIN] },
    canActivate: [UserRouteAccessService],
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
