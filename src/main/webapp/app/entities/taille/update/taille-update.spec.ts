import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { TailleService } from '../service/taille.service';
import { ITaille } from '../taille.model';

import { TailleFormService } from './taille-form.service';
import { TailleUpdate } from './taille-update';

describe('Taille Management Update Component', () => {
  let comp: TailleUpdate;
  let fixture: ComponentFixture<TailleUpdate>;
  let activatedRoute: ActivatedRoute;
  let tailleFormService: TailleFormService;
  let tailleService: TailleService;

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

    fixture = TestBed.createComponent(TailleUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tailleFormService = TestBed.inject(TailleFormService);
    tailleService = TestBed.inject(TailleService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const taille: ITaille = { id: 10100 };

      activatedRoute.data = of({ taille });
      comp.ngOnInit();

      expect(comp.taille).toEqual(taille);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITaille>();
      const taille = { id: 2407 };
      vitest.spyOn(tailleFormService, 'getTaille').mockReturnValue(taille);
      vitest.spyOn(tailleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taille });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(taille);
      saveSubject.complete();

      // THEN
      expect(tailleFormService.getTaille).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(tailleService.update).toHaveBeenCalledWith(expect.objectContaining(taille));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ITaille>();
      const taille = { id: 2407 };
      vitest.spyOn(tailleFormService, 'getTaille').mockReturnValue({ id: null });
      vitest.spyOn(tailleService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taille: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(taille);
      saveSubject.complete();

      // THEN
      expect(tailleFormService.getTaille).toHaveBeenCalled();
      expect(tailleService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ITaille>();
      const taille = { id: 2407 };
      vitest.spyOn(tailleService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ taille });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tailleService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
