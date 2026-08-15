import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { IPromotion, NewPromotion } from '../promotion.model';

export type PartialUpdatePromotion = Partial<IPromotion> & Pick<IPromotion, 'id'>;

type RestOf<T extends IPromotion | NewPromotion> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

export type RestPromotion = RestOf<IPromotion>;

export type NewRestPromotion = RestOf<NewPromotion>;

export type PartialUpdateRestPromotion = RestOf<PartialUpdatePromotion>;

@Injectable()
export class PromotionsService {
  readonly promotionsParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly promotionsResource = httpResource<RestPromotion[]>(() => {
    const params = this.promotionsParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of promotion that have been fetched. It is updated when the promotionsResource emits a new value.
   * In case of error while fetching the promotions, the signal is set to an empty array.
   */
  readonly promotions = computed(() =>
    (this.promotionsResource.hasValue() ? this.promotionsResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/promotions');

  protected convertValueFromServer(restPromotion: RestPromotion): IPromotion {
    return {
      ...restPromotion,
      dateDebut: restPromotion.dateDebut ? dayjs(restPromotion.dateDebut) : undefined,
      dateFin: restPromotion.dateFin ? dayjs(restPromotion.dateFin) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class PromotionService extends PromotionsService {
  protected readonly http = inject(HttpClient);

  create(promotion: NewPromotion): Observable<IPromotion> {
    const copy = this.convertValueFromClient(promotion);
    return this.http.post<RestPromotion>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(promotion: IPromotion): Observable<IPromotion> {
    const copy = this.convertValueFromClient(promotion);
    return this.http
      .put<RestPromotion>(`${this.resourceUrl}/${encodeURIComponent(this.getPromotionIdentifier(promotion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(promotion: PartialUpdatePromotion): Observable<IPromotion> {
    const copy = this.convertValueFromClient(promotion);
    return this.http
      .patch<RestPromotion>(`${this.resourceUrl}/${encodeURIComponent(this.getPromotionIdentifier(promotion))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<IPromotion> {
    return this.http
      .get<RestPromotion>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<IPromotion[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestPromotion[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getPromotionIdentifier(promotion: Pick<IPromotion, 'id'>): number {
    return promotion.id;
  }

  comparePromotion(o1: Pick<IPromotion, 'id'> | null, o2: Pick<IPromotion, 'id'> | null): boolean {
    return o1 && o2 ? this.getPromotionIdentifier(o1) === this.getPromotionIdentifier(o2) : o1 === o2;
  }

  addPromotionToCollectionIfMissing<Type extends Pick<IPromotion, 'id'>>(
    promotionCollection: Type[],
    ...promotionsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const promotions: Type[] = promotionsToCheck.filter(isPresent);
    if (promotions.length > 0) {
      const promotionCollectionIdentifiers = promotionCollection.map(promotionItem => this.getPromotionIdentifier(promotionItem));
      const promotionsToAdd = promotions.filter(promotionItem => {
        const promotionIdentifier = this.getPromotionIdentifier(promotionItem);
        if (promotionCollectionIdentifiers.includes(promotionIdentifier)) {
          return false;
        }
        promotionCollectionIdentifiers.push(promotionIdentifier);
        return true;
      });
      return [...promotionsToAdd, ...promotionCollection];
    }
    return promotionCollection;
  }

  protected convertValueFromClient<T extends IPromotion | NewPromotion | PartialUpdatePromotion>(promotion: T): RestOf<T> {
    return {
      ...promotion,
      dateDebut: promotion.dateDebut?.toJSON() ?? null,
      dateFin: promotion.dateFin?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestPromotion): IPromotion {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestPromotion[]): IPromotion[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
