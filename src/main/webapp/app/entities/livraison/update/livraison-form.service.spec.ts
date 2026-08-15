import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../livraison.test-samples';

import { LivraisonFormService } from './livraison-form.service';

describe('Livraison Form Service', () => {
  let service: LivraisonFormService;

  beforeEach(() => {
    service = TestBed.inject(LivraisonFormService);
  });

  describe('Service methods', () => {
    describe('createLivraisonFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLivraisonFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            adresse: expect.any(Object),
            ville: expect.any(Object),
            quartier: expect.any(Object),
            telephone: expect.any(Object),
            fraisLivraison: expect.any(Object),
            statutLivraison: expect.any(Object),
          }),
        );
      });

      it('passing ILivraison should create a new form with FormGroup', () => {
        const formGroup = service.createLivraisonFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            adresse: expect.any(Object),
            ville: expect.any(Object),
            quartier: expect.any(Object),
            telephone: expect.any(Object),
            fraisLivraison: expect.any(Object),
            statutLivraison: expect.any(Object),
          }),
        );
      });
    });

    describe('getLivraison', () => {
      it('should return NewLivraison for default Livraison initial value', () => {
        const formGroup = service.createLivraisonFormGroup(sampleWithNewData);

        const livraison = service.getLivraison(formGroup);

        expect(livraison).toMatchObject(sampleWithNewData);
      });

      it('should return NewLivraison for empty Livraison initial value', () => {
        const formGroup = service.createLivraisonFormGroup();

        const livraison = service.getLivraison(formGroup);

        expect(livraison).toMatchObject({});
      });

      it('should return ILivraison', () => {
        const formGroup = service.createLivraisonFormGroup(sampleWithRequiredData);

        const livraison = service.getLivraison(formGroup);

        expect(livraison).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILivraison should not enable id FormControl', () => {
        const formGroup = service.createLivraisonFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLivraison should disable id FormControl', () => {
        const formGroup = service.createLivraisonFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
