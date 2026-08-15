import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IVarianteProduit, NewVarianteProduit } from '../variante-produit.model';

export type PartialUpdateVarianteProduit = Partial<IVarianteProduit> & Pick<IVarianteProduit, 'id'>;

@Injectable()
export class VarianteProduitsService {
  readonly varianteProduitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly varianteProduitsResource = httpResource<IVarianteProduit[]>(() => {
    const params = this.varianteProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of varianteProduit that have been fetched. It is updated when the varianteProduitsResource emits a new value.
   * In case of error while fetching the varianteProduits, the signal is set to an empty array.
   */
  readonly varianteProduits = computed(() => (this.varianteProduitsResource.hasValue() ? this.varianteProduitsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/variante-produits');
}

@Injectable({ providedIn: 'root' })
export class VarianteProduitService extends VarianteProduitsService {
  protected readonly http = inject(HttpClient);

  create(varianteProduit: NewVarianteProduit): Observable<IVarianteProduit> {
    return this.http.post<IVarianteProduit>(this.resourceUrl, varianteProduit);
  }

  update(varianteProduit: IVarianteProduit): Observable<IVarianteProduit> {
    return this.http.put<IVarianteProduit>(
      `${this.resourceUrl}/${encodeURIComponent(this.getVarianteProduitIdentifier(varianteProduit))}`,
      varianteProduit,
    );
  }

  partialUpdate(varianteProduit: PartialUpdateVarianteProduit): Observable<IVarianteProduit> {
    return this.http.patch<IVarianteProduit>(
      `${this.resourceUrl}/${encodeURIComponent(this.getVarianteProduitIdentifier(varianteProduit))}`,
      varianteProduit,
    );
  }

  find(id: number): Observable<IVarianteProduit> {
    return this.http.get<IVarianteProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IVarianteProduit[]>> {
    const options = createRequestOption(req);
    return this.http.get<IVarianteProduit[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getVarianteProduitIdentifier(varianteProduit: Pick<IVarianteProduit, 'id'>): number {
    return varianteProduit.id;
  }

  compareVarianteProduit(o1: Pick<IVarianteProduit, 'id'> | null, o2: Pick<IVarianteProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getVarianteProduitIdentifier(o1) === this.getVarianteProduitIdentifier(o2) : o1 === o2;
  }

  addVarianteProduitToCollectionIfMissing<Type extends Pick<IVarianteProduit, 'id'>>(
    varianteProduitCollection: Type[],
    ...varianteProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const varianteProduits: Type[] = varianteProduitsToCheck.filter(isPresent);
    if (varianteProduits.length > 0) {
      const varianteProduitCollectionIdentifiers = varianteProduitCollection.map(varianteProduitItem =>
        this.getVarianteProduitIdentifier(varianteProduitItem),
      );
      const varianteProduitsToAdd = varianteProduits.filter(varianteProduitItem => {
        const varianteProduitIdentifier = this.getVarianteProduitIdentifier(varianteProduitItem);
        if (varianteProduitCollectionIdentifiers.includes(varianteProduitIdentifier)) {
          return false;
        }
        varianteProduitCollectionIdentifiers.push(varianteProduitIdentifier);
        return true;
      });
      return [...varianteProduitsToAdd, ...varianteProduitCollection];
    }
    return varianteProduitCollection;
  }
}
