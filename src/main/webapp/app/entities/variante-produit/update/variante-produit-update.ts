import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICouleur } from 'app/entities/couleur/couleur.model';
import { CouleurService } from 'app/entities/couleur/service/couleur.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { TailleService } from 'app/entities/taille/service/taille.service';
import { ITaille } from 'app/entities/taille/taille.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { VarianteProduitService } from '../service/variante-produit.service';
import { IVarianteProduit } from '../variante-produit.model';

import { VarianteProduitFormGroup, VarianteProduitFormService } from './variante-produit-form.service';

@Component({
  selector: 'jhi-variante-produit-update',
  templateUrl: './variante-produit-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class VarianteProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  varianteProduit: IVarianteProduit | null = null;

  taillesSharedCollection = signal<ITaille[]>([]);
  couleursSharedCollection = signal<ICouleur[]>([]);
  produitsSharedCollection = signal<IProduit[]>([]);

  protected varianteProduitService = inject(VarianteProduitService);
  protected varianteProduitFormService = inject(VarianteProduitFormService);
  protected tailleService = inject(TailleService);
  protected couleurService = inject(CouleurService);
  protected produitService = inject(ProduitService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: VarianteProduitFormGroup = this.varianteProduitFormService.createVarianteProduitFormGroup();

  compareTaille = (o1: ITaille | null, o2: ITaille | null): boolean => this.tailleService.compareTaille(o1, o2);

  compareCouleur = (o1: ICouleur | null, o2: ICouleur | null): boolean => this.couleurService.compareCouleur(o1, o2);

  compareProduit = (o1: IProduit | null, o2: IProduit | null): boolean => this.produitService.compareProduit(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ varianteProduit }) => {
      this.varianteProduit = varianteProduit;
      if (varianteProduit) {
        this.updateForm(varianteProduit);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const varianteProduit = this.varianteProduitFormService.getVarianteProduit(this.editForm);
    if (varianteProduit.id === null) {
      this.subscribeToSaveResponse(this.varianteProduitService.create(varianteProduit));
    } else {
      this.subscribeToSaveResponse(this.varianteProduitService.update(varianteProduit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IVarianteProduit | null>): void {
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

  protected updateForm(varianteProduit: IVarianteProduit): void {
    this.varianteProduit = varianteProduit;
    this.varianteProduitFormService.resetForm(this.editForm, varianteProduit);

    this.taillesSharedCollection.update(tailles =>
      this.tailleService.addTailleToCollectionIfMissing<ITaille>(tailles, varianteProduit.taille),
    );
    this.couleursSharedCollection.update(couleurs =>
      this.couleurService.addCouleurToCollectionIfMissing<ICouleur>(couleurs, varianteProduit.couleur),
    );
    this.produitsSharedCollection.update(produits =>
      this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, varianteProduit.produit),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.tailleService
      .query()
      .pipe(map((res: HttpResponse<ITaille[]>) => res.body ?? []))
      .pipe(map((tailles: ITaille[]) => this.tailleService.addTailleToCollectionIfMissing<ITaille>(tailles, this.varianteProduit?.taille)))
      .subscribe((tailles: ITaille[]) => this.taillesSharedCollection.set(tailles));

    this.couleurService
      .query()
      .pipe(map((res: HttpResponse<ICouleur[]>) => res.body ?? []))
      .pipe(
        map((couleurs: ICouleur[]) =>
          this.couleurService.addCouleurToCollectionIfMissing<ICouleur>(couleurs, this.varianteProduit?.couleur),
        ),
      )
      .subscribe((couleurs: ICouleur[]) => this.couleursSharedCollection.set(couleurs));

    this.produitService
      .query()
      .pipe(map((res: HttpResponse<IProduit[]>) => res.body ?? []))
      .pipe(
        map((produits: IProduit[]) =>
          this.produitService.addProduitToCollectionIfMissing<IProduit>(produits, this.varianteProduit?.produit),
        ),
      )
      .subscribe((produits: IProduit[]) => this.produitsSharedCollection.set(produits));
  }
}
