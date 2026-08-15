import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICouleur } from '../couleur.model';
import { CouleurService } from '../service/couleur.service';

import { CouleurFormService } from './couleur-form.service';
import { CouleurUpdate } from './couleur-update';

describe('Couleur Management Update Component', () => {
  let comp: CouleurUpdate;
  let fixture: ComponentFixture<CouleurUpdate>;
  let activatedRoute: ActivatedRoute;
  let couleurFormService: CouleurFormService;
  let couleurService: CouleurService;

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

    fixture = TestBed.createComponent(CouleurUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    couleurFormService = TestBed.inject(CouleurFormService);
    couleurService = TestBed.inject(CouleurService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const couleur: ICouleur = { id: 19271 };

      activatedRoute.data = of({ couleur });
      comp.ngOnInit();

      expect(comp.couleur).toEqual(couleur);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICouleur>();
      const couleur = { id: 15106 };
      vitest.spyOn(couleurFormService, 'getCouleur').mockReturnValue(couleur);
      vitest.spyOn(couleurService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ couleur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(couleur);
      saveSubject.complete();

      // THEN
      expect(couleurFormService.getCouleur).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(couleurService.update).toHaveBeenCalledWith(expect.objectContaining(couleur));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICouleur>();
      const couleur = { id: 15106 };
      vitest.spyOn(couleurFormService, 'getCouleur').mockReturnValue({ id: null });
      vitest.spyOn(couleurService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ couleur: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(couleur);
      saveSubject.complete();

      // THEN
      expect(couleurFormService.getCouleur).toHaveBeenCalled();
      expect(couleurService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICouleur>();
      const couleur = { id: 15106 };
      vitest.spyOn(couleurService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ couleur });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(couleurService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
