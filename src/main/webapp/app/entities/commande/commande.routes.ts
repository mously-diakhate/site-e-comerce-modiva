import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CommandeResolve from './route/commande-routing-resolve.service';

const commandeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/commande').then(m => m.Commande),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/commande-detail').then(m => m.CommandeDetail),
    resolve: {
      commande: CommandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/commande-update').then(m => m.CommandeUpdate),
    resolve: {
      commande: CommandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/commande-update').then(m => m.CommandeUpdate),
    resolve: {
      commande: CommandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default commandeRoute;
