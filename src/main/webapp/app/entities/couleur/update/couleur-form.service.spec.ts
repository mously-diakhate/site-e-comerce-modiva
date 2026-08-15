import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../couleur.test-samples';

import { CouleurFormService } from './couleur-form.service';

describe('Couleur Form Service', () => {
  let service: CouleurFormService;

  beforeEach(() => {
    service = TestBed.inject(CouleurFormService);
  });

  describe('Service methods', () => {
    describe('createCouleurFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCouleurFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            libelle: expect.any(Object),
            codeHex: expect.any(Object),
          }),
        );
      });

      it('passing ICouleur should create a new form with FormGroup', () => {
        const formGroup = service.createCouleurFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            libelle: expect.any(Object),
            codeHex: expect.any(Object),
          }),
        );
      });
    });

    describe('getCouleur', () => {
      it('should return NewCouleur for default Couleur initial value', () => {
        const formGroup = service.createCouleurFormGroup(sampleWithNewData);

        const couleur = service.getCouleur(formGroup);

        expect(couleur).toMatchObject(sampleWithNewData);
      });

      it('should return NewCouleur for empty Couleur initial value', () => {
        const formGroup = service.createCouleurFormGroup();

        const couleur = service.getCouleur(formGroup);

        expect(couleur).toMatchObject({});
      });

      it('should return ICouleur', () => {
        const formGroup = service.createCouleurFormGroup(sampleWithRequiredData);

        const couleur = service.getCouleur(formGroup);

        expect(couleur).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICouleur should not enable id FormControl', () => {
        const formGroup = service.createCouleurFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCouleur should disable id FormControl', () => {
        const formGroup = service.createCouleurFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
