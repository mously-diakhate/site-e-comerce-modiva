import { Routes } from '@angular/router';

import { ASC } from 'app/config/navigation.constants';
import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';

import CategorieResolve from './route/categorie-routing-resolve.service';

const categorieRoute: Routes = [
  {
    path: '',
    loadComponent: () => import('./list/categorie').then(m => m.Categorie),
    data: {
      defaultSort: `id,${ASC}`,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    loadComponent: () => import('./detail/categorie-detail').then(m => m.CategorieDetail),
    resolve: {
      categorie: CategorieResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    loadComponent: () => import('./update/categorie-update').then(m => m.CategorieUpdate),
    resolve: {
      categorie: CategorieResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    loadComponent: () => import('./update/categorie-update').then(m => m.CategorieUpdate),
    resolve: {
      categorie: CategorieResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default categorieRoute;
