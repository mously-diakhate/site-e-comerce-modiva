import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import LigneCommandeResolve from './route/ligne-commande-routing-resolve.service';

const ligneCommandeRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/ligne-commande').then(m => m.LigneCommande),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/ligne-commande-detail').then(m => m.LigneCommandeDetail),
    resolve: {
      ligneCommande: LigneCommandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/ligne-commande-update').then(m => m.LigneCommandeUpdate),
    resolve: {
      ligneCommande: LigneCommandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/ligne-commande-update').then(m => m.LigneCommandeUpdate),
    resolve: {
      ligneCommande: LigneCommandeResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default ligneCommandeRoute;
