import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { PromotionService } from 'app/entities/promotion/service/promotion.service';
import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';

import { ProduitFormService } from './produit-form.service';
import { ProduitUpdate } from './produit-update';

describe('Produit Management Update Component', () => {
  let comp: ProduitUpdate;
  let fixture: ComponentFixture<ProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let produitFormService: ProduitFormService;
  let produitService: ProduitService;
  let promotionService: PromotionService;
  let categorieService: CategorieService;

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

    fixture = TestBed.createComponent(ProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    produitFormService = TestBed.inject(ProduitFormService);
    produitService = TestBed.inject(ProduitService);
    promotionService = TestBed.inject(PromotionService);
    categorieService = TestBed.inject(CategorieService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Promotion query and add missing value', () => {
      const produit: IProduit = { id: 21239 };
      const promotions: IPromotion[] = [{ id: 30739 }];
      produit.promotions = promotions;

      const promotionCollection: IPromotion[] = [{ id: 30739 }];
      vitest.spyOn(promotionService, 'query').mockReturnValue(of(new HttpResponse({ body: promotionCollection })));
      const additionalPromotions = [...promotions];
      const expectedCollection: IPromotion[] = [...additionalPromotions, ...promotionCollection];
      vitest.spyOn(promotionService, 'addPromotionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(promotionService.query).toHaveBeenCalled();
      expect(promotionService.addPromotionToCollectionIfMissing).toHaveBeenCalledWith(
        promotionCollection,
        ...additionalPromotions.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.promotionsSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Categorie query and add missing value', () => {
      const produit: IProduit = { id: 21239 };
      const categorie: ICategorie = { id: 7213 };
      produit.categorie = categorie;

      const categorieCollection: ICategorie[] = [{ id: 7213 }];
      vitest.spyOn(categorieService, 'query').mockReturnValue(of(new HttpResponse({ body: categorieCollection })));
      const additionalCategories = [categorie];
      const expectedCollection: ICategorie[] = [...additionalCategories, ...categorieCollection];
      vitest.spyOn(categorieService, 'addCategorieToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(categorieService.query).toHaveBeenCalled();
      expect(categorieService.addCategorieToCollectionIfMissing).toHaveBeenCalledWith(
        categorieCollection,
        ...additionalCategories.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.categoriesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const produit: IProduit = { id: 21239 };
      const promotion: IPromotion = { id: 30739 };
      produit.promotions = [promotion];
      const categorie: ICategorie = { id: 7213 };
      produit.categorie = categorie;

      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      expect(comp.promotionsSharedCollection()).toContainEqual(promotion);
      expect(comp.categoriesSharedCollection()).toContainEqual(categorie);
      expect(comp.produit).toEqual(produit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProduit>();
      const produit = { id: 28529 };
      vitest.spyOn(produitFormService, 'getProduit').mockReturnValue(produit);
      vitest.spyOn(produitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(produit);
      saveSubject.complete();

      // THEN
      expect(produitFormService.getProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(produitService.update).toHaveBeenCalledWith(expect.objectContaining(produit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IProduit>();
      const produit = { id: 28529 };
      vitest.spyOn(produitFormService, 'getProduit').mockReturnValue({ id: null });
      vitest.spyOn(produitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(produit);
      saveSubject.complete();

      // THEN
      expect(produitFormService.getProduit).toHaveBeenCalled();
      expect(produitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IProduit>();
      const produit = { id: 28529 };
      vitest.spyOn(produitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(produitService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('comparePromotion', () => {
      it('should forward to promotionService', () => {
        const entity = { id: 30739 };
        const entity2 = { id: 21560 };
        vitest.spyOn(promotionService, 'comparePromotion');
        comp.comparePromotion(entity, entity2);
        expect(promotionService.comparePromotion).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCategorie', () => {
      it('should forward to categorieService', () => {
        const entity = { id: 7213 };
        const entity2 = { id: 18684 };
        vitest.spyOn(categorieService, 'compareCategorie');
        comp.compareCategorie(entity, entity2);
        expect(categorieService.compareCategorie).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
