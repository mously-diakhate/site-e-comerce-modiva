import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import TailleResolve from './route/taille-routing-resolve.service';

const tailleRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/taille').then(m => m.Taille),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/taille-detail').then(m => m.TailleDetail),
    resolve: {
      taille: TailleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/taille-update').then(m => m.TailleUpdate),
    resolve: {
      taille: TailleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/taille-update').then(m => m.TailleUpdate),
    resolve: {
      taille: TailleResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default tailleRoute;
