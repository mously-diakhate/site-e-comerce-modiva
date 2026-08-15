import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StatutLivraison } from 'app/entities/enumerations/statut-livraison.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILivraison } from '../livraison.model';
import { LivraisonService } from '../service/livraison.service';

import { LivraisonFormGroup, LivraisonFormService } from './livraison-form.service';

@Component({
  selector: 'jhi-livraison-update',
  templateUrl: './livraison-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class LivraisonUpdate implements OnInit {
  readonly isSaving = signal(false);
  livraison: ILivraison | null = null;
  statutLivraisonValues = Object.keys(StatutLivraison);

  protected livraisonService = inject(LivraisonService);
  protected livraisonFormService = inject(LivraisonFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: LivraisonFormGroup = this.livraisonFormService.createLivraisonFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ livraison }) => {
      this.livraison = livraison;
      if (livraison) {
        this.updateForm(livraison);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const livraison = this.livraisonFormService.getLivraison(this.editForm);
    if (livraison.id === null) {
      this.subscribeToSaveResponse(this.livraisonService.create(livraison));
    } else {
      this.subscribeToSaveResponse(this.livraisonService.update(livraison));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ILivraison | null>): void {
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

  protected updateForm(livraison: ILivraison): void {
    this.livraison = livraison;
    this.livraisonFormService.resetForm(this.editForm, livraison);
  }
}
