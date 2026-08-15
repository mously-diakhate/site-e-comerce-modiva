import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../variante-produit.test-samples';

import { VarianteProduitFormService } from './variante-produit-form.service';

describe('VarianteProduit Form Service', () => {
  let service: VarianteProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(VarianteProduitFormService);
  });

  describe('Service methods', () => {
    describe('createVarianteProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createVarianteProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            stock: expect.any(Object),
            taille: expect.any(Object),
            couleur: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing IVarianteProduit should create a new form with FormGroup', () => {
        const formGroup = service.createVarianteProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            stock: expect.any(Object),
            taille: expect.any(Object),
            couleur: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getVarianteProduit', () => {
      it('should return NewVarianteProduit for default VarianteProduit initial value', () => {
        const formGroup = service.createVarianteProduitFormGroup(sampleWithNewData);

        const varianteProduit = service.getVarianteProduit(formGroup);

        expect(varianteProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewVarianteProduit for empty VarianteProduit initial value', () => {
        const formGroup = service.createVarianteProduitFormGroup();

        const varianteProduit = service.getVarianteProduit(formGroup);

        expect(varianteProduit).toMatchObject({});
      });

      it('should return IVarianteProduit', () => {
        const formGroup = service.createVarianteProduitFormGroup(sampleWithRequiredData);

        const varianteProduit = service.getVarianteProduit(formGroup);

        expect(varianteProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IVarianteProduit should not enable id FormControl', () => {
        const formGroup = service.createVarianteProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewVarianteProduit should disable id FormControl', () => {
        const formGroup = service.createVarianteProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
