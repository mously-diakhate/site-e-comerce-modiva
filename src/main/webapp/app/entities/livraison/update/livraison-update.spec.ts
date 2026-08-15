import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ILivraison } from '../livraison.model';
import { LivraisonService } from '../service/livraison.service';

import { LivraisonFormService } from './livraison-form.service';
import { LivraisonUpdate } from './livraison-update';

describe('Livraison Management Update Component', () => {
  let comp: LivraisonUpdate;
  let fixture: ComponentFixture<LivraisonUpdate>;
  let activatedRoute: ActivatedRoute;
  let livraisonFormService: LivraisonFormService;
  let livraisonService: LivraisonService;

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

    fixture = TestBed.createComponent(LivraisonUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    livraisonFormService = TestBed.inject(LivraisonFormService);
    livraisonService = TestBed.inject(LivraisonService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const livraison: ILivraison = { id: 8459 };

      activatedRoute.data = of({ livraison });
      comp.ngOnInit();

      expect(comp.livraison).toEqual(livraison);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILivraison>();
      const livraison = { id: 11785 };
      vitest.spyOn(livraisonFormService, 'getLivraison').mockReturnValue(livraison);
      vitest.spyOn(livraisonService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livraison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(livraison);
      saveSubject.complete();

      // THEN
      expect(livraisonFormService.getLivraison).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(livraisonService.update).toHaveBeenCalledWith(expect.objectContaining(livraison));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ILivraison>();
      const livraison = { id: 11785 };
      vitest.spyOn(livraisonFormService, 'getLivraison').mockReturnValue({ id: null });
      vitest.spyOn(livraisonService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livraison: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(livraison);
      saveSubject.complete();

      // THEN
      expect(livraisonFormService.getLivraison).toHaveBeenCalled();
      expect(livraisonService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ILivraison>();
      const livraison = { id: 11785 };
      vitest.spyOn(livraisonService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ livraison });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(livraisonService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
