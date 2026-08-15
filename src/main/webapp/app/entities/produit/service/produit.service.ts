import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IProduit, NewProduit } from '../produit.model';

export type PartialUpdateProduit = Partial<IProduit> & Pick<IProduit, 'id'>;

@Injectable()
export class ProduitsService {
  readonly produitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly produitsResource = httpResource<IProduit[]>(() => {
    const params = this.produitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of produit that have been fetched. It is updated when the produitsResource emits a new value.
   * In case of error while fetching the produits, the signal is set to an empty array.
   */
  readonly produits = computed(() => (this.produitsResource.hasValue() ? this.produitsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/produits');
}

@Injectable({ providedIn: 'root' })
export class ProduitService extends ProduitsService {
  protected readonly http = inject(HttpClient);

  create(produit: NewProduit): Observable<IProduit> {
    return this.http.post<IProduit>(this.resourceUrl, produit);
  }

  update(produit: IProduit): Observable<IProduit> {
    return this.http.put<IProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getProduitIdentifier(produit))}`, produit);
  }

  partialUpdate(produit: PartialUpdateProduit): Observable<IProduit> {
    return this.http.patch<IProduit>(`${this.resourceUrl}/${encodeURIComponent(this.getProduitIdentifier(produit))}`, produit);
  }

  find(id: number): Observable<IProduit> {
    return this.http.get<IProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IProduit[]>> {
    const options = createRequestOption(req);
    return this.http.get<IProduit[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getProduitIdentifier(produit: Pick<IProduit, 'id'>): number {
    return produit.id;
  }

  compareProduit(o1: Pick<IProduit, 'id'> | null, o2: Pick<IProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getProduitIdentifier(o1) === this.getProduitIdentifier(o2) : o1 === o2;
  }

  addProduitToCollectionIfMissing<Type extends Pick<IProduit, 'id'>>(
    produitCollection: Type[],
    ...produitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const produits: Type[] = produitsToCheck.filter(isPresent);
    if (produits.length > 0) {
      const produitCollectionIdentifiers = produitCollection.map(produitItem => this.getProduitIdentifier(produitItem));
      const produitsToAdd = produits.filter(produitItem => {
        const produitIdentifier = this.getProduitIdentifier(produitItem);
        if (produitCollectionIdentifiers.includes(produitIdentifier)) {
          return false;
        }
        produitCollectionIdentifiers.push(produitIdentifier);
        return true;
      });
      return [...produitsToAdd, ...produitCollection];
    }
    return produitCollection;
  }
}
