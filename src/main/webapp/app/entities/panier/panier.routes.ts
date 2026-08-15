import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PanierResolve from './route/panier-routing-resolve.service';

const panierRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/panier').then(m => m.Panier),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/panier-detail').then(m => m.PanierDetail),
    resolve: {
      panier: PanierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/panier-update').then(m => m.PanierUpdate),
    resolve: {
      panier: PanierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/panier-update').then(m => m.PanierUpdate),
    resolve: {
      panier: PanierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default panierRoute;
