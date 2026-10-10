import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Component, OnInit, effect, inject, signal, untracked } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Data, ParamMap, Router, RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { NgbPagination } from '@ng-bootstrap/ng-bootstrap/pagination';
import { TranslateModule } from '@ngx-translate/core';
import { Subscription, combineLatest, filter, tap } from 'rxjs';

import { DEFAULT_SORT_DATA, ITEM_DELETED_EVENT, SORT } from 'app/config/navigation.constants';
import { ITEMS_PER_PAGE, PAGE_HEADER, TOTAL_COUNT_RESPONSE_HEADER } from 'app/config/pagination.constants';
import { DataUtils } from 'app/core/util/data-util.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { Filter, FilterOptions, IFilterOption, IFilterOptions } from 'app/shared/filter';
import { TranslateDirective } from 'app/shared/language';
import { ItemCount } from 'app/shared/pagination';
import { SortByDirective, SortDirective, SortService, type SortState, sortStateSignal } from 'app/shared/sort';
import { IImageProduit } from 'app/entities/image-produit/image-produit.model';
import { ProduitDeleteDialog } from '../delete/produit-delete-dialog';
import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';

const SORT_OPTIONS: { value: string; label: string }[] = [
  { value: 'id,desc', label: 'Plus récents' },
  { value: 'nom,asc', label: 'Nom (A → Z)' },
  { value: 'prix,asc', label: 'Prix croissant' },
  { value: 'prix,desc', label: 'Prix décroissant' },
];

interface CouleurSummary {
  id: number;
  libelle: string;
  codeHex: string;
}

interface TailleSummary {
  id: number;
  libelle: string;
}

interface PromotionSummary {
  id: number;
  nom: string;
}

interface ProduitSwatches {
  couleurs: CouleurSummary[];
  tailles: TailleSummary[];
  promotions: PromotionSummary[];
}

interface ProduitListExtra {
  produitId: number;
  imageUrl: string | null;
  couleurs: CouleurSummary[];
  tailles: TailleSummary[];
  promotions: PromotionSummary[];
}

@Component({
  selector: 'jhi-produit',
  templateUrl: './produit.html',
  styleUrl: './produit.scss',
  imports: [
    RouterLink,
    FormsModule,
    FontAwesomeModule,
    AlertError,
    Alert,
    SortDirective,
    SortByDirective,
    TranslateDirective,
    TranslateModule,
    Filter,
    NgbPagination,
    ItemCount,
  ],
})
export class Produit implements OnInit {
  subscription: Subscription | null = null;
  readonly produits = signal<IProduit[]>([]);

  readonly firstImages = signal<Record<number, string>>({});
  readonly productSwatches = signal<Record<number, ProduitSwatches>>({});

  readonly productImages = signal<Record<number, string[]>>({});
  readonly hoveredProductId = signal<number | null>(null);
  readonly hoverImageIndex = signal(0);

  sortState = sortStateSignal({});
  filters: IFilterOptions = new FilterOptions();
  readonly sortOptions = SORT_OPTIONS;

  readonly itemsPerPage = signal(ITEMS_PER_PAGE);
  readonly totalItems = signal(0);
  readonly page = signal(1);
  protected http = inject(HttpClient);

  readonly router = inject(Router);
  protected readonly produitService = inject(ProduitService);
  // eslint-disable-next-line @typescript-eslint/member-ordering
  readonly isLoading = this.produitService.produitsResource.isLoading;
  protected readonly activatedRoute = inject(ActivatedRoute);
  protected readonly sortService = inject(SortService);
  protected readonly filterOptions = toSignal(this.filters.filterChanges);
  protected dataUtils = inject(DataUtils);
  protected modalService = inject(NgbModal);

  constructor() {
    effect(() => {
      const headers = this.produitService.produitsResource.headers();
      if (headers) {
        this.fillComponentAttributesFromResponseHeader(headers);
      }
    });
    effect(() => {
      const produits = this.fillComponentAttributesFromResponseBody([...this.produitService.produits()]);
      this.produits.set(produits);
      this.loadProductExtras(produits);
      this.loadProductImages(produits);
    });

    effect(() => {
      const filterOptions = this.filterOptions();
      if (filterOptions) {
        untracked(() => {
          this.handleNavigation(1, this.sortState(), filterOptions);
        });
      }
    });
  }

  trackId = (item: IProduit): number => this.produitService.getProduitIdentifier(item);

  ngOnInit(): void {
    this.subscription = combineLatest([this.activatedRoute.queryParamMap, this.activatedRoute.data])
      .pipe(
        tap(([params, data]) => this.fillComponentAttributeFromRoute(params, data)),
        tap(() => this.load()),
      )
      .subscribe();
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    return this.dataUtils.openFile(base64String, contentType);
  }

  delete(produit: IProduit): void {
    const modalRef = this.modalService.open(ProduitDeleteDialog, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.produit = produit;
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

  statutLabel(statut: string | null | undefined): string {
    return statut === 'ACTIF' ? 'Actif' : statut === 'INACTIF' ? 'Inactif' : '—';
  }

  onSortOptionChange(event: Event): void {
    const value = (event.target as HTMLSelectElement).value;
    const [predicate, direction] = value.split(',');
    this.navigateToWithComponentValues({ predicate, ascending: direction === 'asc' } as SortState);
  }

  navigateToWithComponentValues(event: SortState): void {
    this.handleNavigation(this.page(), event, this.filters.filterOptions);
  }

  navigateToPage(page: number): void {
    this.handleNavigation(page, this.sortState(), this.filters.filterOptions);
  }

  onThumbEnter(produitId: number | undefined): void {
    if (produitId == null) {
      return;
    }
    const images = this.productImages()[produitId];
    if (!images || images.length <= 1) {
      return;
    }
    this.hoveredProductId.set(produitId);
    this.hoverImageIndex.set(0);
  }

  onThumbMove(event: MouseEvent, produitId: number | undefined): void {
    if (produitId == null || this.hoveredProductId() !== produitId) {
      return;
    }
    const images = this.productImages()[produitId];
    if (!images || images.length <= 1) {
      return;
    }
    const target = event.currentTarget as HTMLElement;
    const rect = target.getBoundingClientRect();
    const ratio = (event.clientX - rect.left) / rect.width;
    const index = Math.min(images.length - 1, Math.max(0, Math.floor(ratio * images.length)));
    this.hoverImageIndex.set(index);
  }

  onThumbLeave(): void {
    this.hoveredProductId.set(null);
    this.hoverImageIndex.set(0);
  }

  currentThumbUrl(produit: IProduit): string | null {
    if (produit.id == null) {
      return null;
    }
    const images = this.productImages()[produit.id];
    if (this.hoveredProductId() === produit.id && images && images.length > 0) {
      return images[this.hoverImageIndex() % images.length];
    }
    return this.firstImages()[produit.id] ?? images?.[0] ?? null;
  }

  protected fillComponentAttributeFromRoute(params: ParamMap, data: Data): void {
    const page = params.get(PAGE_HEADER);
    this.page.set(+(page ?? 1));
    this.sortState.set(this.sortService.parseSortParam(params.get(SORT) ?? data[DEFAULT_SORT_DATA]));
    this.filters.initializeFromParams(params);
  }

  protected fillComponentAttributesFromResponseBody(data: IProduit[]): IProduit[] {
    return data;
  }

  protected fillComponentAttributesFromResponseHeader(headers: HttpHeaders): void {
    this.totalItems.set(Number(headers.get(TOTAL_COUNT_RESPONSE_HEADER)));
  }

  protected queryBackend(): void {
    const pageToLoad: number = this.page();
    const queryObject: any = {
      page: pageToLoad - 1,
      size: this.itemsPerPage(),
      eagerload: true,
      sort: this.sortService.buildSortParam(this.sortState()),
    };
    for (const filterOption of this.filters.filterOptions) {
      queryObject[filterOption.name] = filterOption.values;
    }
    this.produitService.produitsParams.set(queryObject);
  }

  protected handleNavigation(page: number, sortState: SortState, filterOptions?: IFilterOption[]): void {
    const queryParamsObj: any = {
      page,
      size: this.itemsPerPage(),
      sort: this.sortService.buildSortParam(sortState),
    };

    if (filterOptions) {
      for (const filterOption of filterOptions) {
        queryParamsObj[filterOption.nameAsQueryParam()] = filterOption.values;
      }
    }

    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: queryParamsObj,
    });
  }

  protected loadProductExtras(produits: IProduit[]): void {
    const ids = produits.map(p => p.id).filter((id): id is number => id != null);

    if (ids.length === 0) {
      this.firstImages.set({});
      this.productSwatches.set({});
      return;
    }

    let params = new HttpParams();
    ids.forEach(id => {
      params = params.append('ids', id);
    });

    this.http.get<ProduitListExtra[]>('api/produits/list-extras', { params }).subscribe(extras => {
      const images: Record<number, string> = {};
      const swatches: Record<number, ProduitSwatches> = {};

      extras.forEach(e => {
        if (e.imageUrl) {
          images[e.produitId] = e.imageUrl;
        }
        swatches[e.produitId] = { couleurs: e.couleurs ?? [], tailles: e.tailles ?? [], promotions: e.promotions ?? [] };
      });

      this.firstImages.set(images);
      this.productSwatches.set(swatches);
    });
  }

  protected loadProductImages(produits: IProduit[]): void {
    produits.forEach(p => {
      if (p.id == null) {
        return;
      }
      this.http.get<IImageProduit[]>(`api/image-produits/by-produit/${p.id}`).subscribe(images => {
        const urls = (images ?? [])
          .slice()
          .sort((a, b) => (a.ordre ?? 0) - (b.ordre ?? 0))
          .map(img => img.url)
          .filter((u): u is string => !!u);
        this.productImages.update(map => ({ ...map, [p.id as number]: urls }));
      });
    });
  }
}
