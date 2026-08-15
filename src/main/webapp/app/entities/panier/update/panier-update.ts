import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IPanier } from '../panier.model';
import { PanierService } from '../service/panier.service';

import { PanierFormGroup, PanierFormService } from './panier-form.service';

@Component({
  selector: 'jhi-panier-update',
  templateUrl: './panier-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class PanierUpdate implements OnInit {
  readonly isSaving = signal(false);
  panier: IPanier | null = null;

  protected panierService = inject(PanierService);
  protected panierFormService = inject(PanierFormService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: PanierFormGroup = this.panierFormService.createPanierFormGroup();

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ panier }) => {
      this.panier = panier;
      if (panier) {
        this.updateForm(panier);
      }
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const panier = this.panierFormService.getPanier(this.editForm);
    if (panier.id === null) {
      this.subscribeToSaveResponse(this.panierService.create(panier));
    } else {
      this.subscribeToSaveResponse(this.panierService.update(panier));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IPanier | null>): void {
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

  protected updateForm(panier: IPanier): void {
    this.panier = panier;
    this.panierFormService.resetForm(this.editForm, panier);
  }
}
