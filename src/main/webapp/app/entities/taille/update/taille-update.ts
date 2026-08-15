import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { TailleService } from '../service/taille.service';
import { ITaille } from '../taille.model';

import { TailleFormGroup, TailleFormService } from './taille-form.service';

@Component({
  selector: 'jhi-taille-update',
  templateUrl: './taille-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class TailleUpdate implements OnInit {
  readonly isSaving = signal(false);
  taille: ITaille | null = null;

  protected tailleService = inject(TailleService);
  protected tailleFormService = inject(TailleFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: TailleFormGroup = this.tailleFormService.createTailleFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ taille }) => {
      this.taille = taille;
      if (taille) {
        this.updateForm(taille);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const taille = this.tailleFormService.getTaille(this.editForm);
    if (taille.id === null) {
      this.subscribeToSaveResponse(this.tailleService.create(taille));
    } else {
      this.subscribeToSaveResponse(this.tailleService.update(taille));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ITaille | null>): void {
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

  protected updateForm(taille: ITaille): void {
    this.taille = taille;
    this.tailleFormService.resetForm(this.editForm, taille);
  }
}
