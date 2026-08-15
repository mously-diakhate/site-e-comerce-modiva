import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { IImageProduit } from '../image-produit.model';
import { ImageProduitService } from '../service/image-produit.service';

import { ImageProduitFormService } from './image-produit-form.service';
import { ImageProduitUpdate } from './image-produit-update';

describe('ImageProduit Management Update Component', () => {
  let comp: ImageProduitUpdate;
  let fixture: ComponentFixture<ImageProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let imageProduitFormService: ImageProduitFormService;
  let imageProduitService: ImageProduitService;
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

    fixture = TestBed.createComponent(ImageProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    imageProduitFormService = TestBed.inject(ImageProduitFormService);
    imageProduitService = TestBed.inject(ImageProduitService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Produit query and add missing value', () => {
      const imageProduit: IImageProduit = { id: 204 };
      const produit: IProduit = { id: 28529 };
      imageProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ imageProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const imageProduit: IImageProduit = { id: 204 };
      const produit: IProduit = { id: 28529 };
      imageProduit.produit = produit;

      activatedRoute.data = of({ imageProduit });
      comp.ngOnInit();

      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.imageProduit).toEqual(imageProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IImageProduit>();
      const imageProduit = { id: 15619 };
      vitest.spyOn(imageProduitFormService, 'getImageProduit').mockReturnValue(imageProduit);
      vitest.spyOn(imageProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ imageProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(imageProduit);
      saveSubject.complete();

      // THEN
      expect(imageProduitFormService.getImageProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(imageProduitService.update).toHaveBeenCalledWith(expect.objectContaining(imageProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IImageProduit>();
      const imageProduit = { id: 15619 };
      vitest.spyOn(imageProduitFormService, 'getImageProduit').mockReturnValue({ id: null });
      vitest.spyOn(imageProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ imageProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(imageProduit);
      saveSubject.complete();

      // THEN
      expect(imageProduitFormService.getImageProduit).toHaveBeenCalled();
      expect(imageProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IImageProduit>();
      const imageProduit = { id: 15619 };
      vitest.spyOn(imageProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ imageProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(imageProduitService.update).toHaveBeenCalled();
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
