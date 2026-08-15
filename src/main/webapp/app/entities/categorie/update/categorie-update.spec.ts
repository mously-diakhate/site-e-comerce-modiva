import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { ICategorie } from '../categorie.model';
import { CategorieService } from '../service/categorie.service';

import { CategorieFormService } from './categorie-form.service';
import { CategorieUpdate } from './categorie-update';

describe('Categorie Management Update Component', () => {
  let comp: CategorieUpdate;
  let fixture: ComponentFixture<CategorieUpdate>;
  let activatedRoute: ActivatedRoute;
  let categorieFormService: CategorieFormService;
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

    fixture = TestBed.createComponent(CategorieUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    categorieFormService = TestBed.inject(CategorieFormService);
    categorieService = TestBed.inject(CategorieService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should update editForm', () => {
      const categorie: ICategorie = { id: 18684 };

      activatedRoute.data = of({ categorie });
      comp.ngOnInit();

      expect(comp.categorie).toEqual(categorie);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICategorie>();
      const categorie = { id: 7213 };
      vitest.spyOn(categorieFormService, 'getCategorie').mockReturnValue(categorie);
      vitest.spyOn(categorieService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categorie });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(categorie);
      saveSubject.complete();

      // THEN
      expect(categorieFormService.getCategorie).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(categorieService.update).toHaveBeenCalledWith(expect.objectContaining(categorie));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<ICategorie>();
      const categorie = { id: 7213 };
      vitest.spyOn(categorieFormService, 'getCategorie').mockReturnValue({ id: null });
      vitest.spyOn(categorieService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categorie: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(categorie);
      saveSubject.complete();

      // THEN
      expect(categorieFormService.getCategorie).toHaveBeenCalled();
      expect(categorieService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<ICategorie>();
      const categorie = { id: 7213 };
      vitest.spyOn(categorieService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ categorie });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(categorieService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
