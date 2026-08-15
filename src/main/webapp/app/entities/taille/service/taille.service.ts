import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ITaille, NewTaille } from '../taille.model';

export type PartialUpdateTaille = Partial<ITaille> & Pick<ITaille, 'id'>;

@Injectable()
export class TaillesService {
  readonly taillesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly taillesResource = httpResource<ITaille[]>(() => {
    const params = this.taillesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of taille that have been fetched. It is updated when the taillesResource emits a new value.
   * In case of error while fetching the tailles, the signal is set to an empty array.
   */
  readonly tailles = computed(() => (this.taillesResource.hasValue() ? this.taillesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/tailles');
}

@Injectable({ providedIn: 'root' })
export class TailleService extends TaillesService {
  protected readonly http = inject(HttpClient);

  create(taille: NewTaille): Observable<ITaille> {
    return this.http.post<ITaille>(this.resourceUrl, taille);
  }

  update(taille: ITaille): Observable<ITaille> {
    return this.http.put<ITaille>(`${this.resourceUrl}/${encodeURIComponent(this.getTailleIdentifier(taille))}`, taille);
  }

  partialUpdate(taille: PartialUpdateTaille): Observable<ITaille> {
    return this.http.patch<ITaille>(`${this.resourceUrl}/${encodeURIComponent(this.getTailleIdentifier(taille))}`, taille);
  }

  find(id: number): Observable<ITaille> {
    return this.http.get<ITaille>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ITaille[]>> {
    const options = createRequestOption(req);
    return this.http.get<ITaille[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getTailleIdentifier(taille: Pick<ITaille, 'id'>): number {
    return taille.id;
  }

  compareTaille(o1: Pick<ITaille, 'id'> | null, o2: Pick<ITaille, 'id'> | null): boolean {
    return o1 && o2 ? this.getTailleIdentifier(o1) === this.getTailleIdentifier(o2) : o1 === o2;
  }

  addTailleToCollectionIfMissing<Type extends Pick<ITaille, 'id'>>(
    tailleCollection: Type[],
    ...taillesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const tailles: Type[] = taillesToCheck.filter(isPresent);
    if (tailles.length > 0) {
      const tailleCollectionIdentifiers = tailleCollection.map(tailleItem => this.getTailleIdentifier(tailleItem));
      const taillesToAdd = tailles.filter(tailleItem => {
        const tailleIdentifier = this.getTailleIdentifier(tailleItem);
        if (tailleCollectionIdentifiers.includes(tailleIdentifier)) {
          return false;
        }
        tailleCollectionIdentifiers.push(tailleIdentifier);
        return true;
      });
      return [...taillesToAdd, ...tailleCollection];
    }
    return tailleCollection;
  }
}
