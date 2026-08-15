import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../paiement.test-samples';

import { PaiementFormService } from './paiement-form.service';

describe('Paiement Form Service', () => {
  let service: PaiementFormService;

  beforeEach(() => {
    service = TestBed.inject(PaiementFormService);
  });

  describe('Service methods', () => {
    describe('createPaiementFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPaiementFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            montant: expect.any(Object),
            datePaiement: expect.any(Object),
            moyenPaiement: expect.any(Object),
            referenceTransaction: expect.any(Object),
            statutPaiement: expect.any(Object),
          }),
        );
      });

      it('passing IPaiement should create a new form with FormGroup', () => {
        const formGroup = service.createPaiementFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            montant: expect.any(Object),
            datePaiement: expect.any(Object),
            moyenPaiement: expect.any(Object),
            referenceTransaction: expect.any(Object),
            statutPaiement: expect.any(Object),
          }),
        );
      });
    });

    describe('getPaiement', () => {
      it('should return NewPaiement for default Paiement initial value', () => {
        const formGroup = service.createPaiementFormGroup(sampleWithNewData);

        const paiement = service.getPaiement(formGroup);

        expect(paiement).toMatchObject(sampleWithNewData);
      });

      it('should return NewPaiement for empty Paiement initial value', () => {
        const formGroup = service.createPaiementFormGroup();

        const paiement = service.getPaiement(formGroup);

        expect(paiement).toMatchObject({});
      });

      it('should return IPaiement', () => {
        const formGroup = service.createPaiementFormGroup(sampleWithRequiredData);

        const paiement = service.getPaiement(formGroup);

        expect(paiement).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPaiement should not enable id FormControl', () => {
        const formGroup = service.createPaiementFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPaiement should disable id FormControl', () => {
        const formGroup = service.createPaiementFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
