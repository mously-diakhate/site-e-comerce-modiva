import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICouleur } from '../couleur.model';
import { CouleurService } from '../service/couleur.service';

import { CouleurFormGroup, CouleurFormService } from './couleur-form.service';

@Component({
  selector: 'jhi-couleur-update',
  templateUrl: './couleur-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CouleurUpdate implements OnInit {
  readonly isSaving = signal(false);
  couleur: ICouleur | null = null;

  protected couleurService = inject(CouleurService);
  protected couleurFormService = inject(CouleurFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CouleurFormGroup = this.couleurFormService.createCouleurFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ couleur }) => {
      this.couleur = couleur;
      if (couleur) {
        this.updateForm(couleur);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const couleur = this.couleurFormService.getCouleur(this.editForm);
    if (couleur.id === null) {
      this.subscribeToSaveResponse(this.couleurService.create(couleur));
    } else {
      this.subscribeToSaveResponse(this.couleurService.update(couleur));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICouleur | null>): void {
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

  protected updateForm(couleur: ICouleur): void {
    this.couleur = couleur;
    this.couleurFormService.resetForm(this.editForm, couleur);
  }
}
