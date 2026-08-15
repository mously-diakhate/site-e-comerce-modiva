import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IClienteProfile } from 'app/entities/cliente-profile/cliente-profile.model';
import { ClienteProfileService } from 'app/entities/cliente-profile/service/cliente-profile.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAdresse } from '../adresse.model';
import { AdresseService } from '../service/adresse.service';

import { AdresseFormGroup, AdresseFormService } from './adresse-form.service';

@Component({
  selector: 'jhi-adresse-update',
  templateUrl: './adresse-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class AdresseUpdate implements OnInit {
  readonly isSaving = signal(false);
  adresse: IAdresse | null = null;

  clienteProfilesSharedCollection = signal<IClienteProfile[]>([]);

  protected adresseService = inject(AdresseService);
  protected adresseFormService = inject(AdresseFormService);
  protected clienteProfileService = inject(ClienteProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: AdresseFormGroup = this.adresseFormService.createAdresseFormGroup();

  compareClienteProfile = (o1: IClienteProfile | null, o2: IClienteProfile | null): boolean =>
    this.clienteProfileService.compareClienteProfile(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ adresse }) => {
      this.adresse = adresse;
      if (adresse) {
        this.updateForm(adresse);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const adresse = this.adresseFormService.getAdresse(this.editForm);
    if (adresse.id === null) {
      this.subscribeToSaveResponse(this.adresseService.create(adresse));
    } else {
      this.subscribeToSaveResponse(this.adresseService.update(adresse));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IAdresse | null>): void {
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

  protected updateForm(adresse: IAdresse): void {
    this.adresse = adresse;
    this.adresseFormService.resetForm(this.editForm, adresse);

    this.clienteProfilesSharedCollection.update(clienteProfiles =>
      this.clienteProfileService.addClienteProfileToCollectionIfMissing<IClienteProfile>(clienteProfiles, adresse.clienteProfile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clienteProfileService
      .query()
      .pipe(map((res: HttpResponse<IClienteProfile[]>) => res.body ?? []))
      .pipe(
        map((clienteProfiles: IClienteProfile[]) =>
          this.clienteProfileService.addClienteProfileToCollectionIfMissing<IClienteProfile>(clienteProfiles, this.adresse?.clienteProfile),
        ),
      )
      .subscribe((clienteProfiles: IClienteProfile[]) => this.clienteProfilesSharedCollection.set(clienteProfiles));
  }
}
