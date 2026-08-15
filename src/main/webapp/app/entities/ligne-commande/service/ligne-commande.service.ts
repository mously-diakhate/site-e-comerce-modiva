import { HttpClient, HttpResponse, httpResource } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';

import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { ILigneCommande, NewLigneCommande } from '../ligne-commande.model';

export type PartialUpdateLigneCommande = Partial<ILigneCommande> & Pick<ILigneCommande, 'id'>;

@Injectable()
export class LigneCommandesService {
  readonly ligneCommandesParams = signal<Record<string, string | number | boolean | readonly (string | number | boolean)[]> | undefined>(
    undefined,
  );
  readonly ligneCommandesResource = httpResource<ILigneCommande[]>(() => {
    const params = this.ligneCommandesParams();
    if (!params) {
      return undefined;
    }
    return { url: this.resourceUrl, params };
  });
  /**
   * This signal holds the list of ligneCommande that have been fetched. It is updated when the ligneCommandesResource emits a new value.
   * In case of error while fetching the ligneCommandes, the signal is set to an empty array.
   */
  readonly ligneCommandes = computed(() => (this.ligneCommandesResource.hasValue() ? this.ligneCommandesResource.value() : []));
  protected readonly applicationConfigService = inject(ApplicationConfigService);
  protected readonly resourceUrl = this.applicationConfigService.getEndpointFor('api/ligne-commandes');
}

@Injectable({ providedIn: 'root' })
export class LigneCommandeService extends LigneCommandesService {
  protected readonly http = inject(HttpClient);

  create(ligneCommande: NewLigneCommande): Observable<ILigneCommande> {
    return this.http.post<ILigneCommande>(this.resourceUrl, ligneCommande);
  }

  update(ligneCommande: ILigneCommande): Observable<ILigneCommande> {
    return this.http.put<ILigneCommande>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneCommandeIdentifier(ligneCommande))}`,
      ligneCommande,
    );
  }

  partialUpdate(ligneCommande: PartialUpdateLigneCommande): Observable<ILigneCommande> {
    return this.http.patch<ILigneCommande>(
      `${this.resourceUrl}/${encodeURIComponent(this.getLigneCommandeIdentifier(ligneCommande))}`,
      ligneCommande,
    );
  }

  find(id: number): Observable<ILigneCommande> {
    return this.http.get<ILigneCommande>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  query(req?: any): Observable<HttpResponse<ILigneCommande[]>> {
    const options = createRequestOption(req);
    return this.http.get<ILigneCommande[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<undefined> {
    return this.http.delete<undefined>(`${this.resourceUrl}/${encodeURIComponent(id)}`);
  }

  getLigneCommandeIdentifier(ligneCommande: Pick<ILigneCommande, 'id'>): number {
    return ligneCommande.id;
  }

  compareLigneCommande(o1: Pick<ILigneCommande, 'id'> | null, o2: Pick<ILigneCommande, 'id'> | null): boolean {
    return o1 && o2 ? this.getLigneCommandeIdentifier(o1) === this.getLigneCommandeIdentifier(o2) : o1 === o2;
  }

  addLigneCommandeToCollectionIfMissing<Type extends Pick<ILigneCommande, 'id'>>(
    ligneCommandeCollection: Type[],
    ...ligneCommandesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const ligneCommandes: Type[] = ligneCommandesToCheck.filter(isPresent);
    if (ligneCommandes.length > 0) {
      const ligneCommandeCollectionIdentifiers = ligneCommandeCollection.map(ligneCommandeItem =>
        this.getLigneCommandeIdentifier(ligneCommandeItem),
      );
      const ligneCommandesToAdd = ligneCommandes.filter(ligneCommandeItem => {
        const ligneCommandeIdentifier = this.getLigneCommandeIdentifier(ligneCommandeItem);
        if (ligneCommandeCollectionIdentifiers.includes(ligneCommandeIdentifier)) {
          return false;
        }
        ligneCommandeCollectionIdentifiers.push(ligneCommandeIdentifier);
        return true;
      });
      return [...ligneCommandesToAdd, ...ligneCommandeCollection];
    }
    return ligneCommandeCollection;
  }
}
