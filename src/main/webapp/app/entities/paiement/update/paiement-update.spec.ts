import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPaiement } from '../paiement.model';
import { PaiementService } from '../service/paiement.service';

import { PaiementFormService } from './paiement-form.service';
import { PaiementUpdate } from './paiement-update';

describe('Paiement Management Update Component', () => {
  let comp: PaiementUpdate;
  let fixture: ComponentFixture<PaiementUpdate>;
  let activatedRoute: ActivatedRoute;
  let paiementFormService: PaiementFormService;
  let paiementService: PaiementService;

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

    fixture = TestBed.createComponent(PaiementUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    paiementFormService = TestBed.inject(PaiementFormService);
    paiementService = TestBed.inject(PaiementService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const paiement: IPaiement = { id: 17769 };

      activatedRoute.data = of({ paiement });
      comp.ngOnInit();

      expect(comp.paiement).toEqual(paiement);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiement>();
      const paiement = { id: 25893 };
      vitest.spyOn(paiementFormService, 'getPaiement').mockReturnValue(paiement);
      vitest.spyOn(paiementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paiement);
      saveSubject.complete();

      // THEN
      expect(paiementFormService.getPaiement).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(paiementService.update).toHaveBeenCalledWith(expect.objectContaining(paiement));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiement>();
      const paiement = { id: 25893 };
      vitest.spyOn(paiementFormService, 'getPaiement').mockReturnValue({ id: null });
      vitest.spyOn(paiementService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiement: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(paiement);
      saveSubject.complete();

      // THEN
      expect(paiementFormService.getPaiement).toHaveBeenCalled();
      expect(paiementService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IPaiement>();
      const paiement = { id: 25893 };
      vitest.spyOn(paiementService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ paiement });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(paiementService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
