import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICategorie, NewCategorie } from '../categorie.model';

export type PartialUpdateCategorie = Partial<ICategorie> & Pick<ICategorie, 'id'>;

@Injectable()
export class CategoriesService {
  readonly categoriesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly categoriesResource = httpResource<ICategorie[]>(() => {
    const params = this.categoriesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of categorie that have been fetched. It is updated when the categoriesResource emits a new value.
   * In case of error while fetching the categories, the signal is set to an empty array.
   */
  readonly categories = computed(() => (this.categoriesResource.hasValue() ? this.categoriesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/categories');
}

@Injectable({ providedIn: 'root' })
export class CategorieService extends CategoriesService {
  protected readonly http = inject(HttpClient);

  create(categorie: NewCategorie): Observable<ICategorie> {
    return this.http.post<ICategorie>(this.resourceUrl, categorie);
  }

  update(categorie: ICategorie): Observable<ICategorie> {
    return this.http.put<ICategorie>(`${this.resourceUrl}/${encodeURIComponent(this.getCategorieIdentifier(categorie))}`, categorie);
  }

  partialUpdate(categorie: PartialUpdateCategorie): Observable<ICategorie> {
    return this.http.patch<ICategorie>(`${this.resourceUrl}/${encodeURIComponent(this.getCategorieIdentifier(categorie))}`, categorie);
  }

  find(id: number): Observable<ICategorie> {
    return this.http.get<ICategorie>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ICategorie[]>> {
    const options = createRequestOption(req);
    return this.http.get<ICategorie[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCategorieIdentifier(categorie: Pick<ICategorie, 'id'>): number {
    return categorie.id;
  }

  compareCategorie(o1: Pick<ICategorie, 'id'> | null, o2: Pick<ICategorie, 'id'> | null): boolean {
    return o1 && o2 ? this.getCategorieIdentifier(o1) === this.getCategorieIdentifier(o2) : o1 === o2;
  }

  addCategorieToCollectionIfMissing<Type extends Pick<ICategorie, 'id'>>(
    categorieCollection: Type[],
    ...categoriesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const categories: Type[] = categoriesToCheck.filter(isPresent);
    if (categories.length > 0) {
      const categorieCollectionIdentifiers = categorieCollection.map(categorieItem => this.getCategorieIdentifier(categorieItem));
      const categoriesToAdd = categories.filter(categorieItem => {
        const categorieIdentifier = this.getCategorieIdentifier(categorieItem);
        if (categorieCollectionIdentifiers.includes(categorieIdentifier)) {
          return false;
        }
        categorieCollectionIdentifiers.push(categorieIdentifier);
        return true;
      });
      return [...categoriesToAdd, ...categorieCollection];
    }
    return categorieCollection;
  }
}
