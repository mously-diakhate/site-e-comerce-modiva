import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { ICategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
import { StatutProduit } from 'app/entities/enumerations/statut-produit.model';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';

import { ProduitFormGroup, ProduitFormService } from './produit-form.service';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { PromotionService } from 'app/entities/promotion/service/promotion.service';

@Component({
  selector: 'jhi-produit-update',
  templateUrl: './produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  produit: IProduit | null = null;
  statutProduitValues = Object.keys(StatutProduit);

  promotionsSharedCollection = signal<IPromotion[]>([]);
  categoriesSharedCollection = signal<ICategorie[]>([]);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected produitService = inject(ProduitService);
  protected produitFormService = inject(ProduitFormService);
  protected promotionService = inject(PromotionService);
  protected categorieService = inject(CategorieService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProduitFormGroup = this.produitFormService.createProduitFormGroup();

  comparePromotion = (o1: IPromotion | null, o2: IPromotion | null): boolean => this.promotionService.comparePromotion(o1, o2);

  compareCategorie = (o1: ICategorie | null, o2: ICategorie | null): boolean => this.categorieService.compareCategorie(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produit }) => {
      this.produit = produit;
      if (produit) {
        this.updateForm(produit);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertErrorModel>('modivaApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const produit = this.produitFormService.getProduit(this.editForm);
    if (produit.id === null) {
      this.subscribeToSaveResponse(this.produitService.create(produit));
    } else {
      this.subscribeToSaveResponse(this.produitService.update(produit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProduit | null>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving.set(false);
  }

  protected updateForm(produit: IProduit): void {
    this.produit = produit;
    this.produitFormService.resetForm(this.editForm, produit);

    this.promotionsSharedCollection.update(promotions =>
      this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(promotions, ...(produit.promotions ?? [])),
    );
    this.categoriesSharedCollection.update(categories =>
      this.categorieService.addCategorieToCollectionIfMissing<ICategorie>(categories, produit.categorie),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.promotionService
      .query()
      .pipe(map((res: HttpResponse<IPromotion[]>) => res.body ?? []))
      .pipe(
        map((promotions: IPromotion[]) =>
          this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(promotions, ...(this.produit?.promotions ?? [])),
        ),
      )
      .subscribe((promotions: IPromotion[]) => this.promotionsSharedCollection.set(promotions));

    this.categorieService
      .query()
      .pipe(map((res: HttpResponse<ICategorie[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategorie[]) =>
          this.categorieService.addCategorieToCollectionIfMissing<ICategorie>(categories, this.produit?.categorie),
        ),
      )
      .subscribe((categories: ICategorie[]) => this.categoriesSharedCollection.set(categories));
  }
}
