import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPaiement, NewPaiement } from '../paiement.model';

export type PartialUpdatePaiement = Partial<IPaiement> & Pick<IPaiement, 'id'>;

type RestOf<T extends IPaiement | NewPaiement> = Omit<T, 'datePaiement'> & {
  datePaiement?: string | null;
};

export type RestPaiement = RestOf<IPaiement>;

export type NewRestPaiement = RestOf<NewPaiement>;

export type PartialUpdateRestPaiement = RestOf<PartialUpdatePaiement>;

@Injectable()
export class PaiementsService {
  readonly paiementsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly paiementsResource = httpResource<RestPaiement[]>(() => {
    const params = this.paiementsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of paiement that have been fetched. It is updated when the paiementsResource emits a new value.
   * In case of error while fetching the paiements, the signal is set to an empty array.
   */
  readonly paiements = computed(() =>
    (this.paiementsResource.hasValue() ? this.paiementsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/paiements');

  protected convertValueFromServer(restPaiement: RestPaiement): IPaiement {
    return {
      ...restPaiement,
      datePaiement: restPaiement.datePaiement ? dayjs(restPaiement.datePaiement) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PaiementService extends PaiementsService {
  protected readonly http = inject(HttpClient);

  create(paiement: NewPaiement): Observable<IPaiement> {
    const copy = this.convertValueFromClient(paiement);
    return this.http.post<RestPaiement>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(paiement: IPaiement): Observable<IPaiement> {
    const copy = this.convertValueFromClient(paiement);
    return this.http
      .put<RestPaiement>(`${this.resourceUrl}/${encodeURIComponent(this.getPaiementIdentifier(paiement))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(paiement: PartialUpdatePaiement): Observable<IPaiement> {
    const copy = this.convertValueFromClient(paiement);
    return this.http
      .patch<RestPaiement>(`${this.resourceUrl}/${encodeURIComponent(this.getPaiementIdentifier(paiement))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPaiement> {
    return this.http
      .get<RestPaiement>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPaiement[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPaiement[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPaiementIdentifier(paiement: Pick<IPaiement, 'id'>): number {
    return paiement.id;
  }

  comparePaiement(o1: Pick<IPaiement, 'id'> | null, o2: Pick<IPaiement, 'id'> | null): boolean {
    return o1 && o2 ? this.getPaiementIdentifier(o1) === this.getPaiementIdentifier(o2) : o1 === o2;
  }

  addPaiementToCollectionIfMissing<Type extends Pick<IPaiement, 'id'>>(
    paiementCollection: Type[],
    ...paiementsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paiements: Type[] = paiementsToCheck.filter(isPresent);
    if (paiements.length > 0) {
      const paiementCollectionIdentifiers = paiementCollection.map(paiementItem => this.getPaiementIdentifier(paiementItem));
      const paiementsToAdd = paiements.filter(paiementItem => {
        const paiementIdentifier = this.getPaiementIdentifier(paiementItem);
        if (paiementCollectionIdentifiers.includes(paiementIdentifier)) {
          return false;
        }
        paiementCollectionIdentifiers.push(paiementIdentifier);
        return true;
      });
      return [...paiementsToAdd, ...paiementCollection];
    }
    return paiementCollection;
  }

  protected convertValueFromClient<T extends IPaiement | NewPaiement | PartialUpdatePaiement>(paiement: T): RestOf<T> {
    return {
      ...paiement,
      datePaiement: paiement.datePaiement?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPaiement): IPaiement {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPaiement[]): IPaiement[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
