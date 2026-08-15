import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IClienteProfile } from '../cliente-profile.model';
import { ClienteProfileService } from '../service/cliente-profile.service';

import { ClienteProfileFormGroup, ClienteProfileFormService } from './cliente-profile-form.service';

@Component({
  selector: 'jhi-cliente-profile-update',
  templateUrl: './cliente-profile-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ClienteProfileUpdate implements OnInit {
  readonly isSaving = signal(false);
  clienteProfile: IClienteProfile | null = null;

  usersSharedCollection = signal<IUser[]>([]);
  paniersCollection = signal<IPanier[]>([]);

  protected clienteProfileService = inject(ClienteProfileService);
  protected clienteProfileFormService = inject(ClienteProfileFormService);
  protected userService = inject(UserService);
  protected panierService = inject(PanierService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ClienteProfileFormGroup = this.clienteProfileFormService.createClienteProfileFormGroup();

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  comparePanier = (o1: IPanier | null, o2: IPanier | null): boolean => this.panierService.comparePanier(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ clienteProfile }) => {
      this.clienteProfile = clienteProfile;
      if (clienteProfile) {
        this.updateForm(clienteProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const clienteProfile = this.clienteProfileFormService.getClienteProfile(this.editForm);
    if (clienteProfile.id === null) {
      this.subscribeToSaveResponse(this.clienteProfileService.create(clienteProfile));
    } else {
      this.subscribeToSaveResponse(this.clienteProfileService.update(clienteProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IClienteProfile | null>): void {
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

  protected updateForm(clienteProfile: IClienteProfile): void {
    this.clienteProfile = clienteProfile;
    this.clienteProfileFormService.resetForm(this.editForm, clienteProfile);

    this.usersSharedCollection.update(users => this.userService.addUserToCollectionIfMissing<IUser>(users, clienteProfile.user));
    this.paniersCollection.set(this.panierService.addPanierToCollectionIfMissing<IPanier>(this.paniersCollection(), clienteProfile.panier));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.clienteProfile?.user)))
      .subscribe((users: IUser[]) => this.usersSharedCollection.set(users));

    this.panierService
      .query({ filter: 'clienteprofile-is-null' })
      .pipe(map((res: HttpResponse<IPanier[]>) => res.body ?? []))
      .pipe(map((paniers: IPanier[]) => this.panierService.addPanierToCollectionIfMissing<IPanier>(paniers, this.clienteProfile?.panier)))
      .subscribe((paniers: IPanier[]) => this.paniersCollection.set(paniers));
  }
}
