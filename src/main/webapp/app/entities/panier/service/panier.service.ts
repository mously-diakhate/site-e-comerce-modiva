import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPanier, NewPanier } from '../panier.model';

export type PartialUpdatePanier = Partial<IPanier> & Pick<IPanier, 'id'>;

type RestOf<T extends IPanier | NewPanier> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

export type RestPanier = RestOf<IPanier>;

export type NewRestPanier = RestOf<NewPanier>;

export type PartialUpdateRestPanier = RestOf<PartialUpdatePanier>;

@Injectable()
export class PaniersService {
  readonly paniersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly paniersResource = httpResource<RestPanier[]>(() => {
    const params = this.paniersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of panier that have been fetched. It is updated when the paniersResource emits a new value.
   * In case of error while fetching the paniers, the signal is set to an empty array.
   */
  readonly paniers = computed(() =>
    (this.paniersResource.hasValue() ? this.paniersResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/paniers');

  protected convertValueFromServer(restPanier: RestPanier): IPanier {
    return {
      ...restPanier,
      dateCreation: restPanier.dateCreation ? dayjs(restPanier.dateCreation) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PanierService extends PaniersService {
  protected readonly http = inject(HttpClient);

  create(panier: NewPanier): Observable<IPanier> {
    const copy = this.convertValueFromClient(panier);
    return this.http.post<RestPanier>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(panier: IPanier): Observable<IPanier> {
    const copy = this.convertValueFromClient(panier);
    return this.http
      .put<RestPanier>(`${this.resourceUrl}/${encodeURIComponent(this.getPanierIdentifier(panier))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(panier: PartialUpdatePanier): Observable<IPanier> {
    const copy = this.convertValueFromClient(panier);
    return this.http
      .patch<RestPanier>(`${this.resourceUrl}/${encodeURIComponent(this.getPanierIdentifier(panier))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPanier> {
    return this.http.get<RestPanier>(`${this.resourceUrl}/${encodeURIComponent(id)}`).pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPanier[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPanier[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPanierIdentifier(panier: Pick<IPanier, 'id'>): number {
    return panier.id;
  }

  comparePanier(o1: Pick<IPanier, 'id'> | null, o2: Pick<IPanier, 'id'> | null): boolean {
    return o1 && o2 ? this.getPanierIdentifier(o1) === this.getPanierIdentifier(o2) : o1 === o2;
  }

  addPanierToCollectionIfMissing<Type extends Pick<IPanier, 'id'>>(
    panierCollection: Type[],
    ...paniersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const paniers: Type[] = paniersToCheck.filter(isPresent);
    if (paniers.length > 0) {
      const panierCollectionIdentifiers = panierCollection.map(panierItem => this.getPanierIdentifier(panierItem));
      const paniersToAdd = paniers.filter(panierItem => {
        const panierIdentifier = this.getPanierIdentifier(panierItem);
        if (panierCollectionIdentifiers.includes(panierIdentifier)) {
          return false;
        }
        panierCollectionIdentifiers.push(panierIdentifier);
        return true;
      });
      return [...paniersToAdd, ...panierCollection];
    }
    return panierCollection;
  }

  protected convertValueFromClient<T extends IPanier | NewPanier | PartialUpdatePanier>(panier: T): RestOf<T> {
    return {
      ...panier,
      dateCreation: panier.dateCreation?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPanier): IPanier {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPanier[]): IPanier[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
