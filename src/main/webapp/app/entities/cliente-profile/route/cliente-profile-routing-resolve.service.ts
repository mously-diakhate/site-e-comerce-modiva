import { HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, Router } from '@angular/router';

import { EMPTY, Observable, of } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { IClienteProfile } from '../cliente-profile.model';
import { ClienteProfileService } from '../service/cliente-profile.service';

const clienteProfileResolve = (route: ActivatedRouteSnapshot): Observable<null | IClienteProfile> => {
  const id = route.params.id;
  if (id) {
    const router = inject(Router);
    const service = inject(ClienteProfileService);
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

export default clienteProfileResolve;
