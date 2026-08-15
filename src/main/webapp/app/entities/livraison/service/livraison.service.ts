import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILivraison, NewLivraison } from '../livraison.model';

export type PartialUpdateLivraison = Partial<ILivraison> & Pick<ILivraison, 'id'>;

@Injectable()
export class LivraisonsService {
  readonly livraisonsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly livraisonsResource = httpResource<ILivraison[]>(() => {
    const params = this.livraisonsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of livraison that have been fetched. It is updated when the livraisonsResource emits a new value.
   * In case of error while fetching the livraisons, the signal is set to an empty array.
   */
  readonly livraisons = computed(() => (this.livraisonsResource.hasValue() ? this.livraisonsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/livraisons');
}

@Injectable({ providedIn: 'root' })
export class LivraisonService extends LivraisonsService {
  protected readonly http = inject(HttpClient);

  create(livraison: NewLivraison): Observable<ILivraison> {
    return this.http.post<ILivraison>(this.resourceUrl, livraison);
  }

  update(livraison: ILivraison): Observable<ILivraison> {
    return this.http.put<ILivraison>(`${this.resourceUrl}/${encodeURIComponent(this.getLivraisonIdentifier(livraison))}`, livraison);
  }

  partialUpdate(livraison: PartialUpdateLivraison): Observable<ILivraison> {
    return this.http.patch<ILivraison>(`${this.resourceUrl}/${encodeURIComponent(this.getLivraisonIdentifier(livraison))}`, livraison);
  }

  find(id: number): Observable<ILivraison> {
    return this.http.get<ILivraison>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILivraison[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILivraison[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLivraisonIdentifier(livraison: Pick<ILivraison, 'id'>): number {
    return livraison.id;
  }

  compareLivraison(o1: Pick<ILivraison, 'id'> | null, o2: Pick<ILivraison, 'id'> | null): boolean {
    return o1 && o2 ? this.getLivraisonIdentifier(o1) === this.getLivraisonIdentifier(o2) : o1 === o2;
  }

  addLivraisonToCollectionIfMissing<Type extends Pick<ILivraison, 'id'>>(
    livraisonCollection: Type[],
    ...livraisonsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const livraisons: Type[] = livraisonsToCheck.filter(isPresent);
    if (livraisons.length > 0) {
      const livraisonCollectionIdentifiers = livraisonCollection.map(livraisonItem => this.getLivraisonIdentifier(livraisonItem));
      const livraisonsToAdd = livraisons.filter(livraisonItem => {
        const livraisonIdentifier = this.getLivraisonIdentifier(livraisonItem);
        if (livraisonCollectionIdentifiers.includes(livraisonIdentifier)) {
          return false;
        }
        livraisonCollectionIdentifiers.push(livraisonIdentifier);
        return true;
      });
      return [...livraisonsToAdd, ...livraisonCollection];
    }
    return livraisonCollection;
  }
}
