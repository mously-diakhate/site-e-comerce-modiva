import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IClienteProfile, NewClienteProfile } from '../cliente-profile.model';

export type PartialUpdateClienteProfile = Partial<IClienteProfile> & Pick<IClienteProfile, 'id'>;

@Injectable()
export class ClienteProfilesService {
  readonly clienteProfilesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly clienteProfilesResource = httpResource<IClienteProfile[]>(() => {
    const params = this.clienteProfilesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of clienteProfile that have been fetched. It is updated when the clienteProfilesResource emits a new value.
   * In case of error while fetching the clienteProfiles, the signal is set to an empty array.
   */
  readonly clienteProfiles = computed(() => (this.clienteProfilesResource.hasValue() ? this.clienteProfilesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/cliente-profiles');
}

@Injectable({ providedIn: 'root' })
export class ClienteProfileService extends ClienteProfilesService {
  protected readonly http = inject(HttpClient);

  create(clienteProfile: NewClienteProfile): Observable<IClienteProfile> {
    return this.http.post<IClienteProfile>(this.resourceUrl, clienteProfile);
  }

  update(clienteProfile: IClienteProfile): Observable<IClienteProfile> {
    return this.http.put<IClienteProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getClienteProfileIdentifier(clienteProfile))}`,
      clienteProfile,
    );
  }

  partialUpdate(clienteProfile: PartialUpdateClienteProfile): Observable<IClienteProfile> {
    return this.http.patch<IClienteProfile>(
      `${this.resourceUrl}/${encodeURIComponent(this.getClienteProfileIdentifier(clienteProfile))}`,
      clienteProfile,
    );
  }

  find(id: number): Observable<IClienteProfile> {
    return this.http.get<IClienteProfile>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<IClienteProfile[]>> {
    const options = createRequestOption(req);
    return this.http.get<IClienteProfile[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getClienteProfileIdentifier(clienteProfile: Pick<IClienteProfile, 'id'>): number {
    return clienteProfile.id;
  }

  compareClienteProfile(o1: Pick<IClienteProfile, 'id'> | null, o2: Pick<IClienteProfile, 'id'> | null): boolean {
    return o1 && o2 ? this.getClienteProfileIdentifier(o1) === this.getClienteProfileIdentifier(o2) : o1 === o2;
  }

  addClienteProfileToCollectionIfMissing<Type extends Pick<IClienteProfile, 'id'>>(
    clienteProfileCollection: Type[],
    ...clienteProfilesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const clienteProfiles: Type[] = clienteProfilesToCheck.filter(isPresent);
    if (clienteProfiles.length > 0) {
      const clienteProfileCollectionIdentifiers = clienteProfileCollection.map(clienteProfileItem =>
        this.getClienteProfileIdentifier(clienteProfileItem),
      );
      const clienteProfilesToAdd = clienteProfiles.filter(clienteProfileItem => {
        const clienteProfileIdentifier = this.getClienteProfileIdentifier(clienteProfileItem);
        if (clienteProfileCollectionIdentifiers.includes(clienteProfileIdentifier)) {
          return false;
        }
        clienteProfileCollectionIdentifiers.push(clienteProfileIdentifier);
        return true;
      });
      return [...clienteProfilesToAdd, ...clienteProfileCollection];
    }
    return clienteProfileCollection;
  }
}
