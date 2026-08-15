import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import VarianteProduitResolve from './route/variante-produit-routing-resolve.service';

const varianteProduitRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/variante-produit').then(m => m.VarianteProduit),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/variante-produit-detail').then(m => m.VarianteProduitDetail),
    resolve: {
      varianteProduit: VarianteProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/variante-produit-update').then(m => m.VarianteProduitUpdate),
    resolve: {
      varianteProduit: VarianteProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/variante-produit-update').then(m => m.VarianteProduitUpdate),
    resolve: {
      varianteProduit: VarianteProduitResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default varianteProduitRoute;
