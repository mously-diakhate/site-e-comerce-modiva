import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../taille.test-samples';

import { TailleFormService } from './taille-form.service';

describe('Taille Form Service', () => {
  let service: TailleFormService;

  beforeEach(() => {
    service = TestBed.inject(TailleFormService);
  });

  describe('Service methods', () => {
    describe('createTailleFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createTailleFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            libelle: expect.any(Object),
          }),
        );
      });

      it('passing ITaille should create a new form with FormGroup', () => {
        const formGroup = service.createTailleFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            libelle: expect.any(Object),
          }),
        );
      });
    });

    describe('getTaille', () => {
      it('should return NewTaille for default Taille initial value', () => {
        const formGroup = service.createTailleFormGroup(sampleWithNewData);

        const taille = service.getTaille(formGroup);

        expect(taille).toMatchObject(sampleWithNewData);
      });

      it('should return NewTaille for empty Taille initial value', () => {
        const formGroup = service.createTailleFormGroup();

        const taille = service.getTaille(formGroup);

        expect(taille).toMatchObject({});
      });

      it('should return ITaille', () => {
        const formGroup = service.createTailleFormGroup(sampleWithRequiredData);

        const taille = service.getTaille(formGroup);

        expect(taille).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ITaille should not enable id FormControl', () => {
        const formGroup = service.createTailleFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewTaille should disable id FormControl', () => {
        const formGroup = service.createTailleFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
