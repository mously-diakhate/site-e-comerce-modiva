import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILignePanier, NewLignePanier } from '../ligne-panier.model';

export type PartialUpdateLignePanier = Partial<ILignePanier> & Pick<ILignePanier, 'id'>;

@Injectable()
export class LignePaniersService {
  readonly lignePaniersParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly lignePaniersResource = httpResource<ILignePanier[]>(() => {
    const params = this.lignePaniersParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of lignePanier that have been fetched. It is updated when the lignePaniersResource emits a new value.
   * In case of error while fetching the lignePaniers, the signal is set to an empty array.
   */
  readonly lignePaniers = computed(() => (this.lignePaniersResource.hasValue() ? this.lignePaniersResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-paniers');
}

@Injectable({ providedIn: 'root' })
export class LignePanierService extends LignePaniersService {
  protected readonly http = inject(HttpClient);

  create(lignePanier: NewLignePanier): Observable<ILignePanier> {
    return this.http.post<ILignePanier>(this.resourceUrl, lignePanier);
  }

  update(lignePanier: ILignePanier): Observable<ILignePanier> {
    return this.http.put<ILignePanier>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLignePanierIdentifier(lignePanier))}`,
      lignePanier,
    );
  }

  partialUpdate(lignePanier: PartialUpdateLignePanier): Observable<ILignePanier> {
    return this.http.patch<ILignePanier>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLignePanierIdentifier(lignePanier))}`,
      lignePanier,
    );
  }

  find(id: number): Observable<ILignePanier> {
    return this.http.get<ILignePanier>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILignePanier[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILignePanier[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLignePanierIdentifier(lignePanier: Pick<ILignePanier, 'id'>): number {
    return lignePanier.id;
  }

  compareLignePanier(o1: Pick<ILignePanier, 'id'> | null, o2: Pick<ILignePanier, 'id'> | null): boolean {
    return o1 && o2 ? this.getLignePanierIdentifier(o1) === this.getLignePanierIdentifier(o2) : o1 === o2;
  }

  addLignePanierToCollectionIfMissing<Type extends Pick<ILignePanier, 'id'>>(
    lignePanierCollection: Type[],
    ...lignePaniersToCheck: (Type | null | undefined)[]
  ): Type[] {
    const lignePaniers: Type[] = lignePaniersToCheck.filter(isPresent);
    if (lignePaniers.length > 0) {
      const lignePanierCollectionIdentifiers = lignePanierCollection.map(lignePanierItem => this.getLignePanierIdentifier(lignePanierItem));
      const lignePaniersToAdd = lignePaniers.filter(lignePanierItem => {
        const lignePanierIdentifier = this.getLignePanierIdentifier(lignePanierItem);
        if (lignePanierCollectionIdentifiers.includes(lignePanierIdentifier)) {
          return false;
        }
        lignePanierCollectionIdentifiers.push(lignePanierIdentifier);
        return true;
      });
      return [...lignePaniersToAdd, ...lignePanierCollection];
    }
    return lignePanierCollection;
  }
}
