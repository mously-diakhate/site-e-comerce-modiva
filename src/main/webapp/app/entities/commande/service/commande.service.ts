import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import dayjs from 'dayjs/esm';
import { Observable, map } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ICommande, NewCommande } from '../commande.model';

export type PartialUpdateCommande = Partial<ICommande> & Pick<ICommande, 'id'>;

type RestOf<T extends ICommande | NewCommande> = Omit<T, 'dateCommande'> & {
  dateCommande?: string | null;
};

export type RestCommande = RestOf<ICommande>;

export type NewRestCommande = RestOf<NewCommande>;

export type PartialUpdateRestCommande = RestOf<PartialUpdateCommande>;

@Injectable()
export class CommandesService {
  readonly commandesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly commandesResource = httpResource<RestCommande[]>(() => {
    const params = this.commandesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of commande that have been fetched. It is updated when the commandesResource emits a new value.
   * In case of error while fetching the commandes, the signal is set to an empty array.
   */
  readonly commandes = computed(() =>
    (this.commandesResource.hasValue() ? this.commandesResource.value() : []).map(item => this.convertValueFromServer(item)),
  );
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/commandes');

  protected convertValueFromServer(restCommande: RestCommande): ICommande {
    return {
      ...restCommande,
      dateCommande: restCommande.dateCommande ? dayjs(restCommande.dateCommande) : undefined,
    };
  }
}

@Injectable({ providedIn: 'root' })
export class CommandeService extends CommandesService {
  protected readonly http = inject(HttpClient);

  create(commande: NewCommande): Observable<ICommande> {
    const copy = this.convertValueFromClient(commande);
    return this.http.post<RestCommande>(this.resourceUrl, copy).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(commande: ICommande): Observable<ICommande> {
    const copy = this.convertValueFromClient(commande);
    return this.http
      .put<RestCommande>(`${this.resourceUrl}/${encodeURIComponent(this.getCommandeIdentifier(commande))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(commande: PartialUpdateCommande): Observable<ICommande> {
    const copy = this.convertValueFromClient(commande);
    return this.http
      .patch<RestCommande>(`${this.resourceUrl}/${encodeURIComponent(this.getCommandeIdentifier(commande))}`, copy)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<ICommande> {
    return this.http
      .get<RestCommande>(`${this.resourceUrl}/${encodeURIComponent(id)}`)
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<HttpResponse<ICommande[]>> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCommande[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => res.clone({ body: this.convertResponseArrayFromServer(res.body!) })));
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getCommandeIdentifier(commande: Pick<ICommande, 'id'>): number {
    return commande.id;
  }

  compareCommande(o1: Pick<ICommande, 'id'> | null, o2: Pick<ICommande, 'id'> | null): boolean {
    return o1 && o2 ? this.getCommandeIdentifier(o1) === this.getCommandeIdentifier(o2) : o1 === o2;
  }

  addCommandeToCollectionIfMissing<Type extends Pick<ICommande, 'id'>>(
    commandeCollection: Type[],
    ...commandesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const commandes: Type[] = commandesToCheck.filter(isPresent);
    if (commandes.length > 0) {
      const commandeCollectionIdentifiers = commandeCollection.map(commandeItem => this.getCommandeIdentifier(commandeItem));
      const commandesToAdd = commandes.filter(commandeItem => {
        const commandeIdentifier = this.getCommandeIdentifier(commandeItem);
        if (commandeCollectionIdentifiers.includes(commandeIdentifier)) {
          return false;
        }
        commandeCollectionIdentifiers.push(commandeIdentifier);
        return true;
      });
      return [...commandesToAdd, ...commandeCollection];
    }
    return commandeCollection;
  }

  protected convertValueFromClient<T extends ICommande | NewCommande | PartialUpdateCommande>(commande: T): RestOf<T> {
    return {
      ...commande,
      dateCommande: commande.dateCommande?.toJSON() ?? null,
    };
  }

  protected convertResponseFromServer(res: RestCommande): ICommande {
    return this.convertValueFromServer(res);
  }

  protected convertResponseArrayFromServer(res: RestCommande[]): ICommande[] {
    return res.map(item => this.convertValueFromServer(item));
  }
}
