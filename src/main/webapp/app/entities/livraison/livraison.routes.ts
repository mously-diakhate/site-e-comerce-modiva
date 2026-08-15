import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LivraisonResolve from './route/livraison-routing-resolve.service';

const livraisonRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/livraison').then(m => m.Livraison),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/livraison-detail').then(m => m.LivraisonDetail),
    resolve: {
      livraison: LivraisonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/livraison-update').then(m => m.LivraisonUpdate),
    resolve: {
      livraison: LivraisonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/livraison-update').then(m => m.LivraisonUpdate),
    resolve: {
      livraison: LivraisonResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default livraisonRoute;
