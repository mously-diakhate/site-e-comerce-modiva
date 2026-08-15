import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ImageProduitResolve from './route/image-produit-routing-resolve.service';

const imageProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/image-produit').then(m => m.ImageProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/image-produit-detail').then(m => m.ImageProduitDetail),
    resolve: {
      imageProduit: ImageProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/image-produit-update').then(m => m.ImageProduitUpdate),
    resolve: {
      imageProduit: ImageProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/image-produit-update').then(m => m.ImageProduitUpdate),
    resolve: {
      imageProduit: ImageProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default imageProduitRoute;
