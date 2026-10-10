import { Component, OnInit, effect, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { ICouleur } from '../couleur.model';
import { CouleurDeleteDialog } from '../delete/couleur-delete-dialog';
import { CouleurService } from '../service/couleur.service';

@Component({
  selector: 'jhi-couleur',
  templateUrl: './couleur.html',
  styleUrl: './couleur.scss',
  imports: [RouterLink, FormsModule, FontAwesomeModule, AlertError, Alert, TranslateDirective, TranslateModule],
})
export class Couleur implements OnInit {
  subscription: Subscription | null = null;
  readonly couleurs = signal<ICouleur[]>([]);

  sortState = sortStateSignal({});

  readonly router = inject(Router);
  protected readonly couleurService = inject(CouleurService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.couleurService.couleursResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      this.couleurs.set(this.fillComponentAttributesFromResponseBody([...this.couleurService.couleurs()]));
    });
  }

  trackId = (item: ICouleur): number => this.couleurService.getCouleurIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => {
          if (this.couleurs().length === 0) {
            this.load();
          }
        }),
      )
      .subscribe();
  }

  delete(couleur: ICouleur): void {
    const modalRef = this.modalService.open(CouleurDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.couleur = couleur;
    modalRef.closed
      .pipe(
        filter(reason => reason === ITEM_DELETED_EVENT),
        tap(() => this.load()),
      )
      .subscribe();
  }

  load(): void {
    this.queryBackend();
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(event);
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
  }

  protected refineData(data: ICouleur[]): ICouleur[] {
    const { predicate, order } = this.sortState();
    return predicate && order ? data.sort(this.sortService.startSort({ predicate, order })) : data;
  }

  protected fillComponentAttributesFromResponseBody(data: ICouleur[]): ICouleur[] {
    return this.refineData(data);
  }

  protected queryBackend(): void {
    const queryObject: any = {
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    this.couleurService.couleursParams.set(queryObject);
  }

  protected handleNavigation(sortState: SortState): void {
    const queryParamsObj = {
      sort: this.sortService.buildSortParam(sortState),
    };

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }
}
