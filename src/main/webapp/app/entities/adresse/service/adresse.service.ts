import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IAdresse, NewAdresse } from '../adresse.model';

export type PartialUpdateAdresse = Partial<IAdresse> & Pick<IAdresse, 'id'>;

@Injectable()
export class AdressesService {
  readonly adressesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly adressesResource = httpResource<IAdresse[]>(() => {
    const params = this.adressesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of adresse that have been fetched. It is updated when the adressesResource emits a new value.
   * In case of error while fetching the adresses, the signal is set to an empty array.
   */
  readonly adresses = computed(() => (this.adressesResource.hasValue() ? this.adressesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/adresses');
}

@Injectable({ providedIn: 'root' })
export class AdresseService extends AdressesService {
  protected readonly http = inject(HttpClient);

  create(adresse: NewAdresse): Observable<IAdresse> {
    return this.http.post<IAdresse>(this.resourceUrl, adresse);
  }

  update(adresse: IAdresse): Observable<IAdresse> {
    return this.http.put<IAdresse>(`${this.resourceUrl}/${encodeURIComponent(this.getAdresseIdentifier(adresse))}`, adresse);
  }

  partialUpdate(adresse: PartialUpdateAdresse): Observable<IAdresse> {
    return this.http.patch<IAdresse>(`${this.resourceUrl}/${encodeURIComponent(this.getAdresseIdentifier(adresse))}`, adresse);
  }

  find(id: number): Observable<IAdresse> {
    return this.http.get<IAdresse>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IAdresse[]>> {
    const options = createRequestOption(req);
    return this.http.get<IAdresse[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getAdresseIdentifier(adresse: Pick<IAdresse, 'id'>): number {
    return adresse.id;
  }

  compareAdresse(o1: Pick<IAdresse, 'id'> | null, o2: Pick<IAdresse, 'id'> | null): boolean {
    return o1 && o2 ? this.getAdresseIdentifier(o1) === this.getAdresseIdentifier(o2) : o1 === o2;
  }

  addAdresseToCollectionIfMissing<Type extends Pick<IAdresse, 'id'>>(
    adresseCollection: Type[],
    ...adressesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const adresses: Type[] = adressesToCheck.filter(isPresent);
    if (adresses.length > 0) {
      const adresseCollectionIdentifiers = adresseCollection.map(adresseItem => this.getAdresseIdentifier(adresseItem));
      const adressesToAdd = adresses.filter(adresseItem => {
        const adresseIdentifier = this.getAdresseIdentifier(adresseItem);
        if (adresseCollectionIdentifiers.includes(adresseIdentifier)) {
          return false;
        }
        adresseCollectionIdentifiers.push(adresseIdentifier);
        return true;
      });
      return [...adressesToAdd, ...adresseCollection];
    }
    return adresseCollection;
  }
}
