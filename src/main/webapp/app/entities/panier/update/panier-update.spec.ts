import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPanier } from '../panier.model';
import { PanierService } from '../service/panier.service';

import { PanierFormService } from './panier-form.service';
import { PanierUpdate } from './panier-update';

describe('Panier Management Update Component', () => {
  let comp: PanierUpdate;
  let fixture: ComponentFixture<PanierUpdate>;
  let activatedRoute: ActivatedRoute;
  let panierFormService: PanierFormService;
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

    fixture = TestBed.createComponent(PanierUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    panierFormService = TestBed.inject(PanierFormService);
    panierService = TestBed.inject(PanierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const panier: IPanier = { id: 8891 };

      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      expect(comp.panier).toEqual(panier);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPanier>();
      const panier = { id: 28828 };
      vitest.spyOn(panierFormService, 'getPanier').mockReturnValue(panier);
      vitest.spyOn(panierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(panier);
      saveSubject.complete();

      // THEN
      expect(panierFormService.getPanier).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(panierService.update).toHaveBeenCalledWith(expect.objectContaining(panier));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPanier>();
      const panier = { id: 28828 };
      vitest.spyOn(panierFormService, 'getPanier').mockReturnValue({ id: null });
      vitest.spyOn(panierService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ panier: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(panier);
      saveSubject.complete();

      // THEN
      expect(panierFormService.getPanier).toHaveBeenCalled();
      expect(panierService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPanier>();
      const panier = { id: 28828 };
      vitest.spyOn(panierService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ panier });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(panierService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
