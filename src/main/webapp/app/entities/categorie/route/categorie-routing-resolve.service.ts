import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { ICategorie } from '../categorie.model';
import { CategorieService } from '../service/categorie.service';

const categorieResolve = (route: ActivatedRouteSnapshot): Observable<null | ICategorie> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(CategorieService);
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

export default categorieResolve;
