import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LignePanierResolve from './route/ligne-panier-routing-resolve.service';

const lignePanierRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-panier').then(m => m.LignePanier),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-panier-detail').then(m => m.LignePanierDetail),
    resolve: {
      lignePanier: LignePanierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-panier-update').then(m => m.LignePanierUpdate),
    resolve: {
      lignePanier: LignePanierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-panier-update').then(m => m.LignePanierUpdate),
    resolve: {
      lignePanier: LignePanierResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default lignePanierRoute;
