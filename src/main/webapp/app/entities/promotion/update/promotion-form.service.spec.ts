import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../promotion.test-samples';

import { PromotionFormService } from './promotion-form.service';

describe('Promotion Form Service', () => {
  let service: PromotionFormService;

  beforeEach(() => {
    service = TestBed.inject(PromotionFormService);
  });

  describe('Service methods', () => {
    describe('createPromotionFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPromotionFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            reduction: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            produits: expect.any(Object),
          }),
        );
      });

      it('passing IPromotion should create a new form with FormGroup', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            nom: expect.any(Object),
            reduction: expect.any(Object),
            dateDebut: expect.any(Object),
            dateFin: expect.any(Object),
            produits: expect.any(Object),
          }),
        );
      });
    });

    describe('getPromotion', () => {
      it('should return NewPromotion for default Promotion initial value', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithNewData);

        const promotion = service.getPromotion(formGroup);

        expect(promotion).toMatchObject(sampleWithNewData);
      });

      it('should return NewPromotion for empty Promotion initial value', () => {
        const formGroup = service.createPromotionFormGroup();

        const promotion = service.getPromotion(formGroup);

        expect(promotion).toMatchObject({});
      });

      it('should return IPromotion', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithRequiredData);

        const promotion = service.getPromotion(formGroup);

        expect(promotion).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPromotion should not enable id FormControl', () => {
        const formGroup = service.createPromotionFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPromotion should disable id FormControl', () => {
        const formGroup = service.createPromotionFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
