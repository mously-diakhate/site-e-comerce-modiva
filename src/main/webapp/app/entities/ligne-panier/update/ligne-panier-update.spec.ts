import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { ILignePanier } from '../ligne-panier.model';
import { LignePanierService } from '../service/ligne-panier.service';

import { LignePanierFormService } from './ligne-panier-form.service';
import { LignePanierUpdate } from './ligne-panier-update';

describe('LignePanier Management Update Component', () => {
  let comp: LignePanierUpdate;
  let fixture: ComponentFixture<LignePanierUpdate>;
  let activatedRoute: ActivatedRoute;
  let lignePanierFormService: LignePanierFormService;
  let lignePanierService: LignePanierService;
  let varianteProduitService: VarianteProduitService;
  let panierService: PanierService;

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

    fixture = TestBed.createComponent(LignePanierUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    lignePanierFormService = TestBed.inject(LignePanierFormService);
    lignePanierService = TestBed.inject(LignePanierService);
    varianteProduitService = TestBed.inject(VarianteProduitService);
    panierService = TestBed.inject(PanierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call VarianteProduit query and add missing value', () => {
      const lignePanier: ILignePanier = { id: 15532 };
      const variante: IVarianteProduit = { id: 24453 };
      lignePanier.variante = variante;

      const varianteProduitCollection: IVarianteProduit[] = [{ id: 24453 }];
      vitest.spyOn(varianteProduitService, 'query').mockReturnValue(of(new HttpResponse({ body: varianteProduitCollection })));
      const additionalVarianteProduits = [variante];
      const expectedCollection: IVarianteProduit[] = [...additionalVarianteProduits, ...varianteProduitCollection];
      vitest.spyOn(varianteProduitService, 'addVarianteProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lignePanier });
      comp.ngOnInit();

      expect(varianteProduitService.query).toHaveBeenCalled();
      expect(varianteProduitService.addVarianteProduitToCollectionIfMissing).toHaveBeenCalledWith(
        varianteProduitCollection,
        ...additionalVarianteProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.varianteProduitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Panier query and add missing value', () => {
      const lignePanier: ILignePanier = { id: 15532 };
      const panier: IPanier = { id: 28828 };
      lignePanier.panier = panier;

      const panierCollection: IPanier[] = [{ id: 28828 }];
      vitest.spyOn(panierService, 'query').mockReturnValue(of(new HttpResponse({ body: panierCollection })));
      const additionalPaniers = [panier];
      const expectedCollection: IPanier[] = [...additionalPaniers, ...panierCollection];
      vitest.spyOn(panierService, 'addPanierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ lignePanier });
      comp.ngOnInit();

      expect(panierService.query).toHaveBeenCalled();
      expect(panierService.addPanierToCollectionIfMissing).toHaveBeenCalledWith(
        panierCollection,
        ...additionalPaniers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.paniersSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const lignePanier: ILignePanier = { id: 15532 };
      const variante: IVarianteProduit = { id: 24453 };
      lignePanier.variante = variante;
      const panier: IPanier = { id: 28828 };
      lignePanier.panier = panier;

      activatedRoute.data = of({ lignePanier });
      comp.ngOnInit();

      expect(comp.varianteProduitsSharedCollection()).toContainEqual(variante);
      expect(comp.paniersSharedCollection()).toContainEqual(panier);
      expect(comp.lignePanier).toEqual(lignePanier);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILignePanier>();
      const lignePanier = { id: 11431 };
      vitest.spyOn(lignePanierFormService, 'getLignePanier').mockReturnValue(lignePanier);
      vitest.spyOn(lignePanierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lignePanier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(lignePanier);
      saveSubject.complete();

      // THEN
      expect(lignePanierFormService.getLignePanier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(lignePanierService.update).toHaveBeenCalledWith(expect.objectContaining(lignePanier));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILignePanier>();
      const lignePanier = { id: 11431 };
      vitest.spyOn(lignePanierFormService, 'getLignePanier').mockReturnValue({ id: null });
      vitest.spyOn(lignePanierService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lignePanier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(lignePanier);
      saveSubject.complete();

      // THEN
      expect(lignePanierFormService.getLignePanier).toHaveBeenCalled();
      expect(lignePanierService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILignePanier>();
      const lignePanier = { id: 11431 };
      vitest.spyOn(lignePanierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ lignePanier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(lignePanierService.update).toHaveBeenCalled();
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

    describe('comparePanier', () => {
      it('should forward to panierService', () => {
        const entity = { id: 28828 };
        const entity2 = { id: 8891 };
        vitest.spyOn(panierService, 'comparePanier');
        comp.comparePanier(entity, entity2);
        expect(panierService.comparePanier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
