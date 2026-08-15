import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IClienteProfile } from 'app/entities/cliente-profile/cliente-profile.model';
import { ClienteProfileService } from 'app/entities/cliente-profile/service/cliente-profile.service';
import { ILivraison } from 'app/entities/livraison/livraison.model';
import { LivraisonService } from 'app/entities/livraison/service/livraison.service';
import { IPaiement } from 'app/entities/paiement/paiement.model';
import { PaiementService } from 'app/entities/paiement/service/paiement.service';
import { ICommande } from '../commande.model';
import { CommandeService } from '../service/commande.service';

import { CommandeFormService } from './commande-form.service';
import { CommandeUpdate } from './commande-update';

describe('Commande Management Update Component', () => {
  let comp: CommandeUpdate;
  let fixture: ComponentFixture<CommandeUpdate>;
  let activatedRoute: ActivatedRoute;
  let commandeFormService: CommandeFormService;
  let commandeService: CommandeService;
  let paiementService: PaiementService;
  let livraisonService: LivraisonService;
  let clienteProfileService: ClienteProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(CommandeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    commandeFormService = TestBed.inject(CommandeFormService);
    commandeService = TestBed.inject(CommandeService);
    paiementService = TestBed.inject(PaiementService);
    livraisonService = TestBed.inject(LivraisonService);
    clienteProfileService = TestBed.inject(ClienteProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call paiement query and add missing value', () => {
      const commande: ICommande = { id: 15466 };
      const paiement: IPaiement = { id: 25893 };
      commande.paiement = paiement;

      const paiementCollection: IPaiement[] = [{ id: 25893 }];
      vitest.spyOn(paiementService, 'query').mockReturnValue(of(new HttpResponse({ body: paiementCollection })));
      const expectedCollection: IPaiement[] = [paiement, ...paiementCollection];
      vitest.spyOn(paiementService, 'addPaiementToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(paiementService.query).toHaveBeenCalled();
      expect(paiementService.addPaiementToCollectionIfMissing).toHaveBeenCalledWith(paiementCollection, paiement);
      expect(comp.paiementsCollection()).toEqual(expectedCollection);
    });

    it('should call livraison query and add missing value', () => {
      const commande: ICommande = { id: 15466 };
      const livraison: ILivraison = { id: 11785 };
      commande.livraison = livraison;

      const livraisonCollection: ILivraison[] = [{ id: 11785 }];
      vitest.spyOn(livraisonService, 'query').mockReturnValue(of(new HttpResponse({ body: livraisonCollection })));
      const expectedCollection: ILivraison[] = [livraison, ...livraisonCollection];
      vitest.spyOn(livraisonService, 'addLivraisonToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(livraisonService.query).toHaveBeenCalled();
      expect(livraisonService.addLivraisonToCollectionIfMissing).toHaveBeenCalledWith(livraisonCollection, livraison);
      expect(comp.livraisonsCollection()).toEqual(expectedCollection);
    });

    it('should call ClienteProfile query and add missing value', () => {
      const commande: ICommande = { id: 15466 };
      const clienteProfile: IClienteProfile = { id: 4325 };
      commande.clienteProfile = clienteProfile;

      const clienteProfileCollection: IClienteProfile[] = [{ id: 4325 }];
      vitest.spyOn(clienteProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteProfileCollection })));
      const additionalClienteProfiles = [clienteProfile];
      const expectedCollection: IClienteProfile[] = [...additionalClienteProfiles, ...clienteProfileCollection];
      vitest.spyOn(clienteProfileService, 'addClienteProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(clienteProfileService.query).toHaveBeenCalled();
      expect(clienteProfileService.addClienteProfileToCollectionIfMissing).toHaveBeenCalledWith(
        clienteProfileCollection,
        ...additionalClienteProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.clienteProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const commande: ICommande = { id: 15466 };
      const paiement: IPaiement = { id: 25893 };
      commande.paiement = paiement;
      const livraison: ILivraison = { id: 11785 };
      commande.livraison = livraison;
      const clienteProfile: IClienteProfile = { id: 4325 };
      commande.clienteProfile = clienteProfile;

      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      expect(comp.paiementsCollection()).toContainEqual(paiement);
      expect(comp.livraisonsCollection()).toContainEqual(livraison);
      expect(comp.clienteProfilesSharedCollection()).toContainEqual(clienteProfile);
      expect(comp.commande).toEqual(commande);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICommande>();
      const commande = { id: 10216 };
      vitest.spyOn(commandeFormService, 'getCommande').mockReturnValue(commande);
      vitest.spyOn(commandeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(commande);
      saveSubject.complete();

      // THEN
      expect(commandeFormService.getCommande).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(commandeService.update).toHaveBeenCalledWith(expect.objectContaining(commande));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICommande>();
      const commande = { id: 10216 };
      vitest.spyOn(commandeFormService, 'getCommande').mockReturnValue({ id: null });
      vitest.spyOn(commandeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commande: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(commande);
      saveSubject.complete();

      // THEN
      expect(commandeFormService.getCommande).toHaveBeenCalled();
      expect(commandeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICommande>();
      const commande = { id: 10216 };
      vitest.spyOn(commandeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ commande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(commandeService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePaiement', () => {
      it('should forward to paiementService', () => {
        const entity = { id: 25893 };
        const entity2 = { id: 17769 };
        vitest.spyOn(paiementService, 'comparePaiement');
        comp.comparePaiement(entity, entity2);
        expect(paiementService.comparePaiement).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareLivraison', () => {
      it('should forward to livraisonService', () => {
        const entity = { id: 11785 };
        const entity2 = { id: 8459 };
        vitest.spyOn(livraisonService, 'compareLivraison');
        comp.compareLivraison(entity, entity2);
        expect(livraisonService.compareLivraison).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareClienteProfile', () => {
      it('should forward to clienteProfileService', () => {
        const entity = { id: 4325 };
        const entity2 = { id: 25314 };
        vitest.spyOn(clienteProfileService, 'compareClienteProfile');
        comp.compareClienteProfile(entity, entity2);
        expect(clienteProfileService.compareClienteProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
