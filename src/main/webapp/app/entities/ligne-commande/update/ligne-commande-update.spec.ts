import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICommande } from 'app/entities/commande/commande.model';
import { CommandeService } from 'app/entities/commande/service/commande.service';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { ILigneCommande } from '../ligne-commande.model';
import { LigneCommandeService } from '../service/ligne-commande.service';

import { LigneCommandeFormService } from './ligne-commande-form.service';
import { LigneCommandeUpdate } from './ligne-commande-update';

describe('LigneCommande Management Update Component', () => {
  let comp: LigneCommandeUpdate;
  let fixture: ComponentFixture<LigneCommandeUpdate>;
  let activatedRoute: ActivatedRoute;
  let ligneCommandeFormService: LigneCommandeFormService;
  let ligneCommandeService: LigneCommandeService;
  let varianteProduitService: VarianteProduitService;
  let commandeService: CommandeService;

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

    fixture = TestBed.createComponent(LigneCommandeUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    ligneCommandeFormService = TestBed.inject(LigneCommandeFormService);
    ligneCommandeService = TestBed.inject(LigneCommandeService);
    varianteProduitService = TestBed.inject(VarianteProduitService);
    commandeService = TestBed.inject(CommandeService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call VarianteProduit query and add missing value', () => {
      const ligneCommande: ILigneCommande = { id: 4281 };
      const variante: IVarianteProduit = { id: 24453 };
      ligneCommande.variante = variante;

      const varianteProduitCollection: IVarianteProduit[] = [{ id: 24453 }];
      vitest.spyOn(varianteProduitService, 'query').mockReturnValue(of(new HttpResponse({ body: varianteProduitCollection })));
      const additionalVarianteProduits = [variante];
      const expectedCollection: IVarianteProduit[] = [...additionalVarianteProduits, ...varianteProduitCollection];
      vitest.spyOn(varianteProduitService, 'addVarianteProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneCommande });
      comp.ngOnInit();

      expect(varianteProduitService.query).toHaveBeenCalled();
      expect(varianteProduitService.addVarianteProduitToCollectionIfMissing).toHaveBeenCalledWith(
        varianteProduitCollection,
        ...additionalVarianteProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.varianteProduitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Commande query and add missing value', () => {
      const ligneCommande: ILigneCommande = { id: 4281 };
      const commande: ICommande = { id: 10216 };
      ligneCommande.commande = commande;

      const commandeCollection: ICommande[] = [{ id: 10216 }];
      vitest.spyOn(commandeService, 'query').mockReturnValue(of(new HttpResponse({ body: commandeCollection })));
      const additionalCommandes = [commande];
      const expectedCollection: ICommande[] = [...additionalCommandes, ...commandeCollection];
      vitest.spyOn(commandeService, 'addCommandeToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ ligneCommande });
      comp.ngOnInit();

      expect(commandeService.query).toHaveBeenCalled();
      expect(commandeService.addCommandeToCollectionIfMissing).toHaveBeenCalledWith(
        commandeCollection,
        ...additionalCommandes.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.commandesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const ligneCommande: ILigneCommande = { id: 4281 };
      const variante: IVarianteProduit = { id: 24453 };
      ligneCommande.variante = variante;
      const commande: ICommande = { id: 10216 };
      ligneCommande.commande = commande;

      activatedRoute.data = of({ ligneCommande });
      comp.ngOnInit();

      expect(comp.varianteProduitsSharedCollection()).toContainEqual(variante);
      expect(comp.commandesSharedCollection()).toContainEqual(commande);
      expect(comp.ligneCommande).toEqual(ligneCommande);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneCommande>();
      const ligneCommande = { id: 21869 };
      vitest.spyOn(ligneCommandeFormService, 'getLigneCommande').mockReturnValue(ligneCommande);
      vitest.spyOn(ligneCommandeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneCommande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneCommande);
      saveSubject.complete();

      // THEN
      expect(ligneCommandeFormService.getLigneCommande).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(ligneCommandeService.update).toHaveBeenCalledWith(expect.objectContaining(ligneCommande));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneCommande>();
      const ligneCommande = { id: 21869 };
      vitest.spyOn(ligneCommandeFormService, 'getLigneCommande').mockReturnValue({ id: null });
      vitest.spyOn(ligneCommandeService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneCommande: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(ligneCommande);
      saveSubject.complete();

      // THEN
      expect(ligneCommandeFormService.getLigneCommande).toHaveBeenCalled();
      expect(ligneCommandeService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILigneCommande>();
      const ligneCommande = { id: 21869 };
      vitest.spyOn(ligneCommandeService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ ligneCommande });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(ligneCommandeService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareVarianteProduit', () => {
      it('should forward to varianteProduitService', () => {
        const entity = { id: 24453 };
        const entity2 = { id: 18856 };
        vitest.spyOn(varianteProduitService, 'compareVarianteProduit');
        comp.compareVarianteProduit(entity, entity2);
        expect(varianteProduitService.compareVarianteProduit).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCommande', () => {
      it('should forward to commandeService', () => {
        const entity = { id: 10216 };
        const entity2 = { id: 15466 };
        vitest.spyOn(commandeService, 'compareCommande');
        comp.compareCommande(entity, entity2);
        expect(commandeService.compareCommande).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
