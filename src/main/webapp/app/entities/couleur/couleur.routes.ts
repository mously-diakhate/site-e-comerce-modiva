import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CouleurResolve from './route/couleur-routing-resolve.service';

const couleurRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/couleur').then(m => m.Couleur),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/couleur-detail').then(m => m.CouleurDetail),
    resolve: {
      couleur: CouleurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/couleur-update').then(m => m.CouleurUpdate),
    resolve: {
      couleur: CouleurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/couleur-update').then(m => m.CouleurUpdate),
    resolve: {
      couleur: CouleurResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default couleurRoute;
