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
import { StatutCommande } from 'app/entities/enumerations/statut-commande.model';
import { ILivraison } from 'app/entities/livraison/livraison.model';
import { LivraisonService } from 'app/entities/livraison/service/livraison.service';
import { IPaiement } from 'app/entities/paiement/paiement.model';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';

import { ICommande } from '../commande.model';
import { CommandeService } from '../service/commande.service';

import { CommandeFormGroup, CommandeFormService } from './commande-form.service';
import { PaiementService } from 'app/entities/paiement/service/paiement.service';

@Component({
  selector: 'jhi-commande-update',
  templateUrl: './commande-update.html',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class CommandeUpdate implements OnInit {
  readonly isSaving = signal(false);
  commande: ICommande | null = null;
  statutCommandeValues = Object.keys(StatutCommande);

  paiementsCollection = signal<IPaiement[]>([]);
  livraisonsCollection = signal<ILivraison[]>([]);
  clienteProfilesSharedCollection = signal<IClienteProfile[]>([]);

  protected commandeService = inject(CommandeService);
  protected commandeFormService = inject(CommandeFormService);
  protected paiementService = inject(PaiementService);
  protected livraisonService = inject(LivraisonService);
  protected clienteProfileService = inject(ClienteProfileService);
  protected activatedRoute = inject(ActivatedRoute);

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: CommandeFormGroup = this.commandeFormService.createCommandeFormGroup();

  comparePaiement = (o1: IPaiement | null, o2: IPaiement | null): boolean => this.paiementService.comparePaiement(o1, o2);

  compareLivraison = (o1: ILivraison | null, o2: ILivraison | null): boolean => this.livraisonService.compareLivraison(o1, o2);

  compareClienteProfile = (o1: IClienteProfile | null, o2: IClienteProfile | null): boolean =>
    this.clienteProfileService.compareClienteProfile(o1, o2);

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

  save(): void {
    this.isSaving.set(true);
    const commande = this.commandeFormService.getCommande(this.editForm);
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

    this.paiementsCollection.set(
      this.paiementService.addPaiementToCollectionIfMissing<IPaiement>(this.paiementsCollection(), commande.paiement),
    );
    this.livraisonsCollection.set(
      this.livraisonService.addLivraisonToCollectionIfMissing<ILivraison>(this.livraisonsCollection(), commande.livraison),
    );
    this.clienteProfilesSharedCollection.update(clienteProfiles =>
      this.clienteProfileService.addClienteProfileToCollectionIfMissing<IClienteProfile>(clienteProfiles, commande.clienteProfile),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.paiementService
      .query({ filter: 'commande-is-null' })
      .pipe(map((res: HttpResponse<IPaiement[]>) => res.body ?? []))
      .pipe(
        map((paiements: IPaiement[]) =>
          this.paiementService.addPaiementToCollectionIfMissing<IPaiement>(paiements, this.commande?.paiement),
        ),
      )
      .subscribe((paiements: IPaiement[]) => this.paiementsCollection.set(paiements));

    this.livraisonService
      .query({ filter: 'commande-is-null' })
      .pipe(map((res: HttpResponse<ILivraison[]>) => res.body ?? []))
      .pipe(
        map((livraisons: ILivraison[]) =>
          this.livraisonService.addLivraisonToCollectionIfMissing<ILivraison>(livraisons, this.commande?.livraison),
        ),
      )
      .subscribe((livraisons: ILivraison[]) => this.livraisonsCollection.set(livraisons));

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
