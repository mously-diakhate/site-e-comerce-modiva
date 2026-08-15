import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import AdresseResolve from './route/adresse-routing-resolve.service';

const adresseRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/adresse').then(m => m.Adresse),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/adresse-detail').then(m => m.AdresseDetail),
    resolve: {
      adresse: AdresseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/adresse-update').then(m => m.AdresseUpdate),
    resolve: {
      adresse: AdresseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/adresse-update').then(m => m.AdresseUpdate),
    resolve: {
      adresse: AdresseResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default adresseRoute;
