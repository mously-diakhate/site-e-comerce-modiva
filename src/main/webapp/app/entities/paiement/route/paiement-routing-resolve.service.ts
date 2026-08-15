import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IPaiement } from '../paiement.model';
import { PaiementService } from '../service/paiement.service';

const paiementResolve = (route: ActivatedRouteSnapshot): Observable<null | IPaiement> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(PaiementService);
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

export default paiementResolve;
