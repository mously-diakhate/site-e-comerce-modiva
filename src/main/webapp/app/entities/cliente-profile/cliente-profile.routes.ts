import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import ClienteProfileResolve from './route/cliente-profile-routing-resolve.service';

const clienteProfileRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/cliente-profile').then(m => m.ClienteProfile),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/cliente-profile-detail').then(m => m.ClienteProfileDetail),
    resolve: {
      clienteProfile: ClienteProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/cliente-profile-update').then(m => m.ClienteProfileUpdate),
    resolve: {
      clienteProfile: ClienteProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/cliente-profile-update').then(m => m.ClienteProfileUpdate),
    resolve: {
      clienteProfile: ClienteProfileResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default clienteProfileRoute;
