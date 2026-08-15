import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICouleur, NewCouleur } from '../couleur.model';

export type PartialUpdateCouleur = Partial<ICouleur> & Pick<ICouleur, 'id'>;

@Injectable()
export class CouleursService {
  readonly couleursParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly couleursResource = httpResource<ICouleur[]>(() => {
    const params = this.couleursParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of couleur that have been fetched. It is updated when the couleursResource emits a new value.
   * In case of error while fetching the couleurs, the signal is set to an empty array.
   */
  readonly couleurs = computed(() => (this.couleursResource.hasValue() ? this.couleursResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/couleurs');
}

@Injectable({ providedIn: 'root' })
export class CouleurService extends CouleursService {
  protected readonly http = inject(HttpClient);

  create(couleur: NewCouleur): Observable<ICouleur> {
    return this.http.post<ICouleur>(this.resourceUrl, couleur);
  }

  update(couleur: ICouleur): Observable<ICouleur> {
    return this.http.put<ICouleur>(`${this.resourceUrl}/${encodeURIComponent(this.getCouleurIdentifier(couleur))}`, couleur);
  }

  partialUpdate(couleur: PartialUpdateCouleur): Observable<ICouleur> {
    return this.http.patch<ICouleur>(`${this.resourceUrl}/${encodeURIComponent(this.getCouleurIdentifier(couleur))}`, couleur);
  }

  find(id: number): Observable<ICouleur> {
    return this.http.get<ICouleur>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICouleur[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICouleur[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCouleurIdentifier(couleur: Pick<ICouleur, 'id'>): number {
    return couleur.id;
  }

  compareCouleur(o1: Pick<ICouleur, 'id'> | null, o2: Pick<ICouleur, 'id'> | null): boolean {
    return o1 && o2 ? this.getCouleurIdentifier(o1) === this.getCouleurIdentifier(o2) : o1 === o2;
  }

  addCouleurToCollectionIfMissing<Type extends Pick<ICouleur, 'id'>>(
    couleurCollection: Type[],
    ...couleursToCheck: (Type | null | undefined)[]
  ): Type[] {
    const couleurs: Type[] = couleursToCheck.filter(isPresent);
    if (couleurs.length > 0) {
      const couleurCollectionIdentifiers = couleurCollection.map(couleurItem => this.getCouleurIdentifier(couleurItem));
      const couleursToAdd = couleurs.filter(couleurItem => {
        const couleurIdentifier = this.getCouleurIdentifier(couleurItem);
        if (couleurCollectionIdentifiers.includes(couleurIdentifier)) {
          return false;
        }
        couleurCollectionIdentifiers.push(couleurIdentifier);
        return true;
      });
      return [...couleursToAdd, ...couleurCollection];
    }
    return couleurCollection;
  }
}
