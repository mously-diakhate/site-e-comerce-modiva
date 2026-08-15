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
import { IImageProduit } from '../image-produit.model';
import { ImageProduitService } from '../service/image-produit.service';

import { ImageProduitFormGroup, ImageProduitFormService } from './image-produit-form.service';

@Component({
  selector: 'jhi-image-produit-update',
  templateUrl: './image-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ImageProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  imageProduit: IImageProduit | null = null;

  produitsSharedCollection = signal<IProduit[]>([]);

  protected imageProduitService = inject(ImageProduitService);
  protected imageProduitFormService = inject(ImageProduitFormService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ImageProduitFormGroup = this.imageProduitFormService.createImageProduitFormGroup();

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ imageProduit }) => {
      this.imageProduit = imageProduit;
      if (imageProduit) {
        this.updateForm(imageProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const imageProduit = this.imageProduitFormService.getImageProduit(this.editForm);
    if (imageProduit.id === null) {
      this.subscribeToSaveResponse(this.imageProduitService.create(imageProduit));
    } else {
      this.subscribeToSaveResponse(this.imageProduitService.update(imageProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IImageProduit | null>): void {
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

  protected updateForm(imageProduit: IImageProduit): void {
    this.imageProduit = imageProduit;
    this.imageProduitFormService.resetForm(this.editForm, imageProduit);

    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, imageProduit.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) => this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.imageProduit?.produit)),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
