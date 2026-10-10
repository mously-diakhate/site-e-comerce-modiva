import { HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IClienteProfile } from 'app/entities/cliente-profile/cliente-profile.model';
import { ClienteProfileService } from 'app/entities/cliente-profile/service/cliente-profile.service';
import { StatutCommande } from 'app/entities/enumerations/statut-commande.model';
import { StatutLivraison } from 'app/entities/enumerations/statut-livraison.model';
import { ILivraison, NewLivraison } from 'app/entities/livraison/livraison.model';
import { LivraisonService } from 'app/entities/livraison/service/livraison.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ICommande, NewCommande } from '../commande.model';
import { CommandeService } from '../service/commande.service';

import { CommandeFormGroup, CommandeFormService } from './commande-form.service';

const MODE_PAIEMENT_LABELS: Record<string, string> = {
  WAVE: 'Wave',
  ORANGE_MONEY: 'Orange Money',
  SUR_PLACE: 'Sur place',
};

interface LivraisonFormValue {
  adresse: string | null;
  ville: string | null;
  quartier: string | null;
  telephone: string | null;
  fraisLivraison: number | null;
  statutLivraison: string | null;
}

@Component({
  selector: 'jhi-commande-update',
  templateUrl: './commande-update.html',
  styleUrl: './commande-update.scss',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CommandeUpdate implements OnInit {
  readonly isSaving = signal(false);
  commande: ICommande | null = null;
  statutCommandeValues = Object.keys(StatutCommande);
  statutLivraisonValues = Object.keys(StatutLivraison);

  clienteProfilesSharedCollection = signal<IClienteProfile[]>([]);

  /** true si la commande nécessite une livraison (sinon retrait en personne). */
  readonly needsLivraison = signal(false);
  /** id de la livraison déjà existante en mode édition (pour update plutôt que create). */
  protected existingLivraisonId: number | null = null;

  protected commandeService = inject(CommandeService);
  protected commandeFormService = inject(CommandeFormService);
  protected livraisonService = inject(LivraisonService);
  protected clienteProfileService = inject(ClienteProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CommandeFormGroup = this.commandeFormService.createCommandeFormGroup();

  /** Sous-formulaire pour les champs de livraison, géré séparément du formulaire principal. */
  readonly livraisonForm = new FormGroup({
    adresse: new FormControl<string | null>(null, { validators: [Validators.required] }),
    ville: new FormControl<string | null>(null, { validators: [Validators.required] }),
    quartier: new FormControl<string | null>(null),
    telephone: new FormControl<string | null>(null),
    fraisLivraison: new FormControl<number | null>(null),
    statutLivraison: new FormControl<string | null>('EN_ATTENTE', { validators: [Validators.required] }),
  });

  compareClienteProfile = (o1: IClienteProfile | null, o2: IClienteProfile | null): boolean =>
    this.clienteProfileService.compareClienteProfile(o1, o2);

  constructor() {
    // Par défaut (création), la commande n'a pas besoin de livraison tant que la case n'est pas cochée.
    this.livraisonForm.disable();
  }

  modePaiementLabel(mode: string | null | undefined): string {
    return mode ? (MODE_PAIEMENT_LABELS[mode] ?? mode) : '—';
  }

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ commande }) => {
      this.commande = commande;
      if (commande) {
        this.updateForm(commande);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    globalThis.history.back();
  }

  /** Active/désactive le sous-formulaire de livraison. */
  toggleNeedsLivraison(checked: boolean): void {
    this.needsLivraison.set(checked);
    if (checked) {
      this.livraisonForm.enable();
    } else {
      this.livraisonForm.disable();
    }
  }

  save(): void {
    this.isSaving.set(true);
    const commande: ICommande | NewCommande = this.commandeFormService.getCommande(this.editForm);

    if (!this.needsLivraison()) {
      // Retrait en personne : pas de livraison à créer.
      this.saveCommande({ ...commande, livraison: null });
      return;
    }

    const v: LivraisonFormValue = this.livraisonForm.getRawValue();
    const livraisonPayload = {
      adresse: v.adresse!,
      ville: v.ville!,
      quartier: v.quartier,
      telephone: v.telephone,
      fraisLivraison: v.fraisLivraison,
      statutLivraison: v.statutLivraison as ILivraison['statutLivraison'],
    };

    const livraisonSave$ = this.existingLivraisonId
      ? this.livraisonService.update({ id: this.existingLivraisonId, ...livraisonPayload })
      : this.livraisonService.create({ id: null, ...livraisonPayload } as NewLivraison);

    livraisonSave$.subscribe({
      next: (savedLivraison: ILivraison) => {
        this.saveCommande({ ...commande, livraison: { id: savedLivraison.id } });
      },
      error: () => {
        this.isSaving.set(false);
      },
    });
  }

  protected saveCommande(commande: ICommande | NewCommande): void {
    if (commande.id === null) {
      this.subscribeToSaveResponse(this.commandeService.create(commande));
    } else {
      this.subscribeToSaveResponse(this.commandeService.update(commande));
    }
  }

  protected subscribeToSaveResponse(result: Observable<ICommande | null>): void {
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

  protected updateForm(commande: ICommande): void {
    this.commande = commande;
    this.commandeFormService.resetForm(this.editForm, commande);

    this.clienteProfilesSharedCollection.update(clienteProfiles =>
      this.clienteProfileService.addClienteProfileToCollectionIfMissing<IClienteProfile>(clienteProfiles, commande.clienteProfile),
    );

    if (commande.livraison?.id) {
      this.existingLivraisonId = commande.livraison.id;
      this.needsLivraison.set(true);
      this.livraisonForm.enable();
      // Le Pick partiel de ICommande.livraison n'a pas quartier/telephone/fraisLivraison : on recharge l'objet complet.
      this.livraisonService.find(commande.livraison.id).subscribe(full => {
        this.livraisonForm.patchValue({
          adresse: full.adresse ?? null,
          ville: full.ville ?? null,
          quartier: full.quartier ?? null,
          telephone: full.telephone ?? null,
          fraisLivraison: full.fraisLivraison ?? null,
          statutLivraison: full.statutLivraison ?? 'EN_ATTENTE',
        });
      });
    } else {
      this.needsLivraison.set(false);
      this.livraisonForm.disable();
    }
  }

  protected loadRelationshipsOptions(): void {
    this.clienteProfileService
      .query()
      .pipe(map((res: HttpResponse<IClienteProfile[]>) => res.body ?? []))
      .pipe(
        map((clienteProfiles: IClienteProfile[]) =>
          this.clienteProfileService.addClienteProfileToCollectionIfMissing<IClienteProfile>(
            clienteProfiles,
            this.commande?.clienteProfile,
          ),
        ),
      )
      .subscribe((clienteProfiles: IClienteProfile[]) => this.clienteProfilesSharedCollection.set(clienteProfiles));
  }
}
