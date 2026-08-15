import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import PaiementResolve from './route/paiement-routing-resolve.service';

const paiementRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/paiement').then(m => m.Paiement),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/paiement-detail').then(m => m.PaiementDetail),
    resolve: {
      paiement: PaiementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/paiement-update').then(m => m.PaiementUpdate),
    resolve: {
      paiement: PaiementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/paiement-update').then(m => m.PaiementUpdate),
    resolve: {
      paiement: PaiementResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default paiementRoute;
