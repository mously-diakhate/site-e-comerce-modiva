import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IPromotion } from '../promotion.model';
import { PromotionService } from '../service/promotion.service';

import { PromotionFormService } from './promotion-form.service';
import { PromotionUpdate } from './promotion-update';

describe('Promotion Management Update Component', () => {
  let comp: PromotionUpdate;
  let fixture: ComponentFixture<PromotionUpdate>;
  let activatedRoute: ActivatedRoute;
  let promotionFormService: PromotionFormService;
  let promotionService: PromotionService;
  let produitService: ProduitService;

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

    fixture = TestBed.createComponent(PromotionUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    promotionFormService = TestBed.inject(PromotionFormService);
    promotionService = TestBed.inject(PromotionService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const promotion: IPromotion = { id: 21560 };
      const produits: IProduit[] = [{ id: 28529 }];
      promotion.produits = produits;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [...produits];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const promotion: IPromotion = { id: 21560 };
      const produit: IProduit = { id: 28529 };
      promotion.produits = [produit];

      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.promotion).toEqual(promotion);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPromotion>();
      const promotion = { id: 30739 };
      vitest.spyOn(promotionFormService, 'getPromotion').mockReturnValue(promotion);
      vitest.spyOn(promotionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(promotion);
      saveSubject.complete();

      // THEN
      expect(promotionFormService.getPromotion).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(promotionService.update).toHaveBeenCalledWith(expect.objectContaining(promotion));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPromotion>();
      const promotion = { id: 30739 };
      vitest.spyOn(promotionFormService, 'getPromotion').mockReturnValue({ id: null });
      vitest.spyOn(promotionService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(promotion);
      saveSubject.complete();

      // THEN
      expect(promotionFormService.getPromotion).toHaveBeenCalled();
      expect(promotionService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPromotion>();
      const promotion = { id: 30739 };
      vitest.spyOn(promotionService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ promotion });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(promotionService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareProduit', () => {
      it('should forward to produitService', () => {
        const entity = { id: 28529 };
        const entity2 = { id: 21239 };
        vitest.spyOn(produitService, 'compareProduit');
        comp.compareProduit(entity, entity2);
        expect(produitService.compareProduit).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
