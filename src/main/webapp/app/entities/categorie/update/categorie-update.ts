import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICategorie } from '../categorie.model';
import { CategorieService } from '../service/categorie.service';

import { CategorieFormGroup, CategorieFormService } from './categorie-form.service';

@Component({
  selector: 'jhi-categorie-update',
  templateUrl: './categorie-update.html',
  styleUrl: './categorie-update.scss',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CategorieUpdate implements OnInit {
  readonly isSaving = signal(false);
  categorie: ICategorie | null = null;

  protected categorieService = inject(CategorieService);
  protected categorieFormService = inject(CategorieFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CategorieFormGroup = this.categorieFormService.createCategorieFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ categorie }) => {
      this.categorie = categorie;
      if (categorie) {
        this.updateForm(categorie);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const categorie = this.categorieFormService.getCategorie(this.editForm);
    if (categorie.id === null) {
      this.subscribeToSaveResponse(this.categorieService.create(categorie));
    } else {
      this.subscribeToSaveResponse(this.categorieService.update(categorie));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICategorie | null>): void {
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

  protected updateForm(categorie: ICategorie): void {
    this.categorie = categorie;
    this.categorieFormService.resetForm(this.editForm, categorie);
  }
}
