import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICouleur } from 'app/entities/couleur/couleur.model';
import { CouleurService } from 'app/entities/couleur/service/couleur.service';
import { IProduit } from 'app/entities/produit/produit.model';
import { ProduitService } from 'app/entities/produit/service/produit.service';
import { TailleService } from 'app/entities/taille/service/taille.service';
import { ITaille } from 'app/entities/taille/taille.model';
import { VarianteProduitService } from '../service/variante-produit.service';
import { IVarianteProduit } from '../variante-produit.model';

import { VarianteProduitFormService } from './variante-produit-form.service';
import { VarianteProduitUpdate } from './variante-produit-update';

describe('VarianteProduit Management Update Component', () => {
  let comp: VarianteProduitUpdate;
  let fixture: ComponentFixture<VarianteProduitUpdate>;
  let activatedRoute: ActivatedRoute;
  let varianteProduitFormService: VarianteProduitFormService;
  let varianteProduitService: VarianteProduitService;
  let tailleService: TailleService;
  let couleurService: CouleurService;
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

    fixture = TestBed.createComponent(VarianteProduitUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    varianteProduitFormService = TestBed.inject(VarianteProduitFormService);
    varianteProduitService = TestBed.inject(VarianteProduitService);
    tailleService = TestBed.inject(TailleService);
    couleurService = TestBed.inject(CouleurService);
    produitService = TestBed.inject(ProduitService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call Taille query and add missing value', () => {
      const varianteProduit: IVarianteProduit = { id: 18856 };
      const taille: ITaille = { id: 2407 };
      varianteProduit.taille = taille;

      const tailleCollection: ITaille[] = [{ id: 2407 }];
      vitest.spyOn(tailleService, 'query').mockReturnValue(of(new HttpResponse({ body: tailleCollection })));
      const additionalTailles = [taille];
      const expectedCollection: ITaille[] = [...additionalTailles, ...tailleCollection];
      vitest.spyOn(tailleService, 'addTailleToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ varianteProduit });
      comp.ngOnInit();

      expect(tailleService.query).toHaveBeenCalled();
      expect(tailleService.addTailleToCollectionIfMissing).toHaveBeenCalledWith(
        tailleCollection,
        ...additionalTailles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.taillesSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Couleur query and add missing value', () => {
      const varianteProduit: IVarianteProduit = { id: 18856 };
      const couleur: ICouleur = { id: 15106 };
      varianteProduit.couleur = couleur;

      const couleurCollection: ICouleur[] = [{ id: 15106 }];
      vitest.spyOn(couleurService, 'query').mockReturnValue(of(new HttpResponse({ body: couleurCollection })));
      const additionalCouleurs = [couleur];
      const expectedCollection: ICouleur[] = [...additionalCouleurs, ...couleurCollection];
      vitest.spyOn(couleurService, 'addCouleurToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ varianteProduit });
      comp.ngOnInit();

      expect(couleurService.query).toHaveBeenCalled();
      expect(couleurService.addCouleurToCollectionIfMissing).toHaveBeenCalledWith(
        couleurCollection,
        ...additionalCouleurs.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.couleursSharedCollection()).toEqual(expectedCollection);
    });

    it('should call Produit query and add missing value', () => {
      const varianteProduit: IVarianteProduit = { id: 18856 };
      const produit: IProduit = { id: 28529 };
      varianteProduit.produit = produit;

      const produitCollection: IProduit[] = [{ id: 28529 }];
      vitest.spyOn(produitService, 'query').mockReturnValue(of(new HttpResponse({ body: produitCollection })));
      const additionalProduits = [produit];
      const expectedCollection: IProduit[] = [...additionalProduits, ...produitCollection];
      vitest.spyOn(produitService, 'addProduitToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ varianteProduit });
      comp.ngOnInit();

      expect(produitService.query).toHaveBeenCalled();
      expect(produitService.addProduitToCollectionIfMissing).toHaveBeenCalledWith(
        produitCollection,
        ...additionalProduits.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.produitsSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const varianteProduit: IVarianteProduit = { id: 18856 };
      const taille: ITaille = { id: 2407 };
      varianteProduit.taille = taille;
      const couleur: ICouleur = { id: 15106 };
      varianteProduit.couleur = couleur;
      const produit: IProduit = { id: 28529 };
      varianteProduit.produit = produit;

      activatedRoute.data = of({ varianteProduit });
      comp.ngOnInit();

      expect(comp.taillesSharedCollection()).toContainEqual(taille);
      expect(comp.couleursSharedCollection()).toContainEqual(couleur);
      expect(comp.produitsSharedCollection()).toContainEqual(produit);
      expect(comp.varianteProduit).toEqual(varianteProduit);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVarianteProduit>();
      const varianteProduit = { id: 24453 };
      vitest.spyOn(varianteProduitFormService, 'getVarianteProduit').mockReturnValue(varianteProduit);
      vitest.spyOn(varianteProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ varianteProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(varianteProduit);
      saveSubject.complete();

      // THEN
      expect(varianteProduitFormService.getVarianteProduit).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(varianteProduitService.update).toHaveBeenCalledWith(expect.objectContaining(varianteProduit));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IVarianteProduit>();
      const varianteProduit = { id: 24453 };
      vitest.spyOn(varianteProduitFormService, 'getVarianteProduit').mockReturnValue({ id: null });
      vitest.spyOn(varianteProduitService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ varianteProduit: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(varianteProduit);
      saveSubject.complete();

      // THEN
      expect(varianteProduitFormService.getVarianteProduit).toHaveBeenCalled();
      expect(varianteProduitService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IVarianteProduit>();
      const varianteProduit = { id: 24453 };
      vitest.spyOn(varianteProduitService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ varianteProduit });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(varianteProduitService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTaille', () => {
      it('should forward to tailleService', () => {
        const entity = { id: 2407 };
        const entity2 = { id: 10100 };
        vitest.spyOn(tailleService, 'compareTaille');
        comp.compareTaille(entity, entity2);
        expect(tailleService.compareTaille).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareCouleur', () => {
      it('should forward to couleurService', () => {
        const entity = { id: 15106 };
        const entity2 = { id: 19271 };
        vitest.spyOn(couleurService, 'compareCouleur');
        comp.compareCouleur(entity, entity2);
        expect(couleurService.compareCouleur).toHaveBeenCalledWith(entity, entity2);
      });
    });

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
