import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'authority',
    data: { pageTitle: 'modivaApp.adminAuthority.home.title' },
    loadChildren: () => import('./admin/authority/authority.routes'),
  },
  {
    path: 'cliente-profile',
    data: { pageTitle: 'modivaApp.clienteProfile.home.title' },
    loadChildren: () => import('./cliente-profile/cliente-profile.routes'),
  },
  {
    path: 'adresse',
    data: { pageTitle: 'modivaApp.adresse.home.title' },
    loadChildren: () => import('./adresse/adresse.routes'),
  },
  {
    path: 'categorie',
    data: { pageTitle: 'modivaApp.categorie.home.title' },
    loadChildren: () => import('./categorie/categorie.routes'),
  },
  {
    path: 'produit',
    data: { pageTitle: 'modivaApp.produit.home.title' },
    loadChildren: () => import('./produit/produit.routes'),
  },
  {
    path: 'image-produit',
    data: { pageTitle: 'modivaApp.imageProduit.home.title' },
    loadChildren: () => import('./image-produit/image-produit.routes'),
  },
  {
    path: 'taille',
    data: { pageTitle: 'modivaApp.taille.home.title' },
    loadChildren: () => import('./taille/taille.routes'),
  },
  {
    path: 'couleur',
    data: { pageTitle: 'modivaApp.couleur.home.title' },
    loadChildren: () => import('./couleur/couleur.routes'),
  },
  {
    path: 'variante-produit',
    data: { pageTitle: 'modivaApp.varianteProduit.home.title' },
    loadChildren: () => import('./variante-produit/variante-produit.routes'),
  },
  {
    path: 'panier',
    data: { pageTitle: 'modivaApp.panier.home.title' },
    loadChildren: () => import('./panier/panier.routes'),
  },
  {
    path: 'ligne-panier',
    data: { pageTitle: 'modivaApp.lignePanier.home.title' },
    loadChildren: () => import('./ligne-panier/ligne-panier.routes'),
  },
  {
    path: 'commande',
    data: { pageTitle: 'modivaApp.commande.home.title' },
    loadChildren: () => import('./commande/commande.routes'),
  },
  {
    path: 'ligne-commande',
    data: { pageTitle: 'modivaApp.ligneCommande.home.title' },
    loadChildren: () => import('./ligne-commande/ligne-commande.routes'),
  },
  {
    path: 'paiement',
    data: { pageTitle: 'modivaApp.paiement.home.title' },
    loadChildren: () => import('./paiement/paiement.routes'),
  },
  {
    path: 'livraison',
    data: { pageTitle: 'modivaApp.livraison.home.title' },
    loadChildren: () => import('./livraison/livraison.routes'),
  },
  {
    path: 'promotion',
    data: { pageTitle: 'modivaApp.promotion.home.title' },
    loadChildren: () => import('./promotion/promotion.routes'),
  },
  {
    path: 'user-management',
    data: { pageTitle: 'userManagement.home.title' },
    loadChildren: () => import('./admin/user-management/user-management.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
