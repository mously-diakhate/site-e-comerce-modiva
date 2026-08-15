import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILignePanier } from '../ligne-panier.model';
import { LignePanierService } from '../service/ligne-panier.service';

import { LignePanierFormGroup, LignePanierFormService } from './ligne-panier-form.service';

@Component({
  selector: 'jhi-ligne-panier-update',
  templateUrl: './ligne-panier-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LignePanierUpdate implements OnInit {
  readonly isSaving = signal(false);
  lignePanier: ILignePanier | null = null;

  varianteProduitsSharedCollection = signal<IVarianteProduit[]>([]);
  paniersSharedCollection = signal<IPanier[]>([]);

  protected lignePanierService = inject(LignePanierService);
  protected lignePanierFormService = inject(LignePanierFormService);
  protected varianteProduitService = inject(VarianteProduitService);
  protected panierService = inject(PanierService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LignePanierFormGroup = this.lignePanierFormService.createLignePanierFormGroup();

  compareVarianteProduit = (o1: IVarianteProduit | null, o2: IVarianteProduit | null): boolean =>
    this.varianteProduitService.compareVarianteProduit(o1, o2);

  comparePanier = (o1: IPanier | null, o2: IPanier | null): boolean => this.panierService.comparePanier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ lignePanier }) => {
      this.lignePanier = lignePanier;
      if (lignePanier) {
        this.updateForm(lignePanier);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const lignePanier = this.lignePanierFormService.getLignePanier(this.editForm);
    if (lignePanier.id === null) {
      this.subscribeToSaveResponse(this.lignePanierService.create(lignePanier));
    } else {
      this.subscribeToSaveResponse(this.lignePanierService.update(lignePanier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILignePanier | null>): void {
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

  protected updateForm(lignePanier: ILignePanier): void {
    this.lignePanier = lignePanier;
    this.lignePanierFormService.resetForm(this.editForm, lignePanier);

    this.varianteProduitsSharedCollection.update(varianteProduits =>
      this.varianteProduitService.addVarianteProduitToCollectionIfMissing<IVarianteProduit>(varianteProduits, lignePanier.variante),
    );
    this.paniersSharedCollection.update(paniers => this.panierService.addPanierToCollectionIfMissing<IPanier>(paniers, lignePanier.panier));
  }

  protected loadRelationshipsOptions(): void {
    this.varianteProduitService
      .query()
      .pipe(map((res: HttpResponse<IVarianteProduit[]>) => res.body ?? []))
      .pipe(
        map((varianteProduits: IVarianteProduit[]) =>
          this.varianteProduitService.addVarianteProduitToCollectionIfMissing<IVarianteProduit>(
            varianteProduits,
            this.lignePanier?.variante,
          ),
        ),
      )
      .subscribe((varianteProduits: IVarianteProduit[]) => this.varianteProduitsSharedCollection.set(varianteProduits));

    this.panierService
      .query()
      .pipe(map((res: HttpResponse<IPanier[]>) => res.body ?? []))
      .pipe(map((paniers: IPanier[]) => this.panierService.addPanierToCollectionIfMissing<IPanier>(paniers, this.lignePanier?.panier)))
      .subscribe((paniers: IPanier[]) => this.paniersSharedCollection.set(paniers));
  }
}
