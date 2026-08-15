import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IImageProduit, NewImageProduit } from '../image-produit.model';

export type PartialUpdateImageProduit = Partial<IImageProduit> & Pick<IImageProduit, 'id'>;

@Injectable()
export class ImageProduitsService {
  readonly imageProduitsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly imageProduitsResource = httpResource<IImageProduit[]>(() => {
    const params = this.imageProduitsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of imageProduit that have been fetched. It is updated when the imageProduitsResource emits a new value.
   * In case of error while fetching the imageProduits, the signal is set to an empty array.
   */
  readonly imageProduits = computed(() => (this.imageProduitsResource.hasValue() ? this.imageProduitsResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/image-produits');
}

@Injectable({ providedIn: 'root' })
export class ImageProduitService extends ImageProduitsService {
  protected readonly http = inject(HttpClient);

  create(imageProduit: NewImageProduit): Observable<IImageProduit> {
    return this.http.post<IImageProduit>(this.resourceUrl, imageProduit);
  }

  update(imageProduit: IImageProduit): Observable<IImageProduit> {
    return this.http.put<IImageProduit>(
      `${this.resourceUrl}/${encodeURIComponent(this.getImageProduitIdentifier(imageProduit))}`,
      imageProduit,
    );
  }

  partialUpdate(imageProduit: PartialUpdateImageProduit): Observable<IImageProduit> {
    return this.http.patch<IImageProduit>(
      `${this.resourceUrl}/${encodeURIComponent(this.getImageProduitIdentifier(imageProduit))}`,
      imageProduit,
    );
  }

  find(id: number): Observable<IImageProduit> {
    return this.http.get<IImageProduit>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IImageProduit[]>> {
    const options = createRequestOption(req);
    return this.http.get<IImageProduit[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getImageProduitIdentifier(imageProduit: Pick<IImageProduit, 'id'>): number {
    return imageProduit.id;
  }

  compareImageProduit(o1: Pick<IImageProduit, 'id'> | null, o2: Pick<IImageProduit, 'id'> | null): boolean {
    return o1 && o2 ? this.getImageProduitIdentifier(o1) === this.getImageProduitIdentifier(o2) : o1 === o2;
  }

  addImageProduitToCollectionIfMissing<Type extends Pick<IImageProduit, 'id'>>(
    imageProduitCollection: Type[],
    ...imageProduitsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const imageProduits: Type[] = imageProduitsToCheck.filter(isPresent);
    if (imageProduits.length > 0) {
      const imageProduitCollectionIdentifiers = imageProduitCollection.map(imageProduitItem =>
        this.getImageProduitIdentifier(imageProduitItem),
      );
      const imageProduitsToAdd = imageProduits.filter(imageProduitItem => {
        const imageProduitIdentifier = this.getImageProduitIdentifier(imageProduitItem);
        if (imageProduitCollectionIdentifiers.includes(imageProduitIdentifier)) {
          return false;
        }
        imageProduitCollectionIdentifiers.push(imageProduitIdentifier);
        return true;
      });
      return [...imageProduitsToAdd, ...imageProduitCollection];
    }
    return imageProduitCollection;
  }
}
