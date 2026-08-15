import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { StatutPaiement } from 'app/entities/enumerations/statut-paiement.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPaiement } from '../paiement.model';
import { PaiementService } from '../service/paiement.service';

import { PaiementFormGroup, PaiementFormService } from './paiement-form.service';

@Component({
  selector: 'jhi-paiement-update',
  templateUrl: './paiement-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PaiementUpdate implements OnInit {
  readonly isSaving = signal(false);
  paiement: IPaiement | null = null;
  statutPaiementValues = Object.keys(StatutPaiement);

  protected paiementService = inject(PaiementService);
  protected paiementFormService = inject(PaiementFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PaiementFormGroup = this.paiementFormService.createPaiementFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ paiement }) => {
      this.paiement = paiement;
      if (paiement) {
        this.updateForm(paiement);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const paiement = this.paiementFormService.getPaiement(this.editForm);
    if (paiement.id === null) {
      this.subscribeToSaveResponse(this.paiementService.create(paiement));
    } else {
      this.subscribeToSaveResponse(this.paiementService.update(paiement));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPaiement | null>): void {
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

  protected updateForm(paiement: IPaiement): void {
    this.paiement = paiement;
    this.paiementFormService.resetForm(this.editForm, paiement);
  }
}
