import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-panier.test-samples';

import { LignePanierFormService } from './ligne-panier-form.service';

describe('LignePanier Form Service', () => {
  let service: LignePanierFormService;

  beforeEach(() => {
    service = TestBed.inject(LignePanierFormService);
  });

  describe('Service methods', () => {
    describe('createLignePanierFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLignePanierFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            variante: expect.any(Object),
            panier: expect.any(Object),
          }),
        );
      });

      it('passing ILignePanier should create a new form with FormGroup', () => {
        const formGroup = service.createLignePanierFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            variante: expect.any(Object),
            panier: expect.any(Object),
          }),
        );
      });
    });

    describe('getLignePanier', () => {
      it('should return NewLignePanier for default LignePanier initial value', () => {
        const formGroup = service.createLignePanierFormGroup(sampleWithNewData);

        const lignePanier = service.getLignePanier(formGroup);

        expect(lignePanier).toMatchObject(sampleWithNewData);
      });

      it('should return NewLignePanier for empty LignePanier initial value', () => {
        const formGroup = service.createLignePanierFormGroup();

        const lignePanier = service.getLignePanier(formGroup);

        expect(lignePanier).toMatchObject({});
      });

      it('should return ILignePanier', () => {
        const formGroup = service.createLignePanierFormGroup(sampleWithRequiredData);

        const lignePanier = service.getLignePanier(formGroup);

        expect(lignePanier).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILignePanier should not enable id FormControl', () => {
        const formGroup = service.createLignePanierFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLignePanier should disable id FormControl', () => {
        const formGroup = service.createLignePanierFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
