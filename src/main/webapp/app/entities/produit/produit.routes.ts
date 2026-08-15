import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ProduitResolve from './route/produit-routing-resolve.service';

const produitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/produit').then(m => m.Produit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/produit-detail').then(m => m.ProduitDetail),
    resolve: {
      produit: ProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/produit-update').then(m => m.ProduitUpdate),
    resolve: {
      produit: ProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/produit-update').then(m => m.ProduitUpdate),
    resolve: {
      produit: ProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default produitRoute;
