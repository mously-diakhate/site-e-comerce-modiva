import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneCommande } from '../ligne-commande.model';
import { LigneCommandeService } from '../service/ligne-commande.service';

import { LigneCommandeFormGroup, LigneCommandeFormService } from './ligne-commande-form.service';

@Component({
  selector: 'jhi-ligne-commande-update',
  templateUrl: './ligne-commande-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LigneCommandeUpdate implements OnInit {
  readonly isSaving = signal(false);
  ligneCommande: ILigneCommande | null = null;

  varianteProduitsSharedCollection = signal<IVarianteProduit[]>([]);
  commandesSharedCollection = signal<ICommande[]>([]);

  protected ligneCommandeService = inject(LigneCommandeService);
  protected ligneCommandeFormService = inject(LigneCommandeFormService);
  protected varianteProduitService = inject(VarianteProduitService);
  protected commandeService = inject(CommandeService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LigneCommandeFormGroup = this.ligneCommandeFormService.createLigneCommandeFormGroup();

  compareVarianteProduit = (o1: IVarianteProduit | null, o2: IVarianteProduit | null): boolean =>
    this.varianteProduitService.compareVarianteProduit(o1, o2);

  compareCommande = (o1: ICommande | null, o2: ICommande | null): boolean => this.commandeService.compareCommande(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ ligneCommande }) => {
      this.ligneCommande = ligneCommande;
      if (ligneCommande) {
        this.updateForm(ligneCommande);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const ligneCommande = this.ligneCommandeFormService.getLigneCommande(this.editForm);
    if (ligneCommande.id === null) {
      this.subscribeToSaveResponse(this.ligneCommandeService.create(ligneCommande));
    } else {
      this.subscribeToSaveResponse(this.ligneCommandeService.update(ligneCommande));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILigneCommande | null>): void {
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

  protected updateForm(ligneCommande: ILigneCommande): void {
    this.ligneCommande = ligneCommande;
    this.ligneCommandeFormService.resetForm(this.editForm, ligneCommande);

    this.varianteProduitsSharedCollection.update(varianteProduits =>
      this.varianteProduitService.addVarianteProduitToCollectionIfMissing<IVarianteProduit>(varianteProduits, ligneCommande.variante),
    );
    this.commandesSharedCollection.update(commandes =>
      this.commandeService.addCommandeToCollectionIfMissing<ICommande>(commandes, ligneCommande.commande),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.varianteProduitService
      .query()
      .pipe(map((res: HttpResponse<IVarianteProduit[]>) => res.body ?? []))
      .pipe(
        map((varianteProduits: IVarianteProduit[]) =>
          this.varianteProduitService.addVarianteProduitToCollectionIfMissing<IVarianteProduit>(
            varianteProduits,
            this.ligneCommande?.variante,
          ),
        ),
      )
      .subscribe((varianteProduits: IVarianteProduit[]) => this.varianteProduitsSharedCollection.set(varianteProduits));

    this.commandeService
      .query()
      .pipe(map((res: HttpResponse<ICommande[]>) => res.body ?? []))
      .pipe(
        map((commandes: ICommande[]) =>
          this.commandeService.addCommandeToCollectionIfMissing<ICommande>(commandes, this.ligneCommande?.commande),
        ),
      )
      .subscribe((commandes: ICommande[]) => this.commandesSharedCollection.set(commandes));
  }
}
