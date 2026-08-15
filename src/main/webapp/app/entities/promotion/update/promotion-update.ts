import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

import { PromotionFormGroup, PromotionFormService } from './promotion-form.service';

@Component({
  selector: 'jhi-promotion-update',
  templateUrl: './promotion-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PromotionUpdate implements OnInit {
  readonly isSaving = signal(false);
  promotion: IPromotion | null = null;

  produitsSharedCollection = signal<IProduit[]>([]);

  protected promotionService = inject(PromotionService);
  protected promotionFormService = inject(PromotionFormService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PromotionFormGroup = this.promotionFormService.createPromotionFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ promotion }) => {
      this.promotion = promotion;
      if (promotion) {
        this.updateForm(promotion);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const promotion = this.promotionFormService.getPromotion(this.editForm);
    if (promotion.id === null) {
      this.subscribeToSaveResponse(this.promotionService.create(promotion));
    } else {
      this.subscribeToSaveResponse(this.promotionService.update(promotion));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPromotion | null>): void {
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

  protected updateForm(promotion: IPromotion): void {
    this.promotion = promotion;
    this.promotionFormService.resetForm(this.editForm, promotion);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ...(promotion.produits ?? [])),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, ...(this.promotion?.produits ?? [])),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
