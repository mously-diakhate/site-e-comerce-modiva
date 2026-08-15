import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ILivraison } from '../livraison.model';
import { LivraisonService } from '../service/livraison.service';

const livraisonResolve = (route: ActivatedRouteSnapshot): Observable<null | ILivraison> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(LivraisonService);
    return service.find(id).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404) {
          router.navigate(['404']);
        } else {
          router.navigate(['error']);
        }
        return EMPTY;
      }),
    );
  }

  return of(null);
};

export default livraisonResolve;
