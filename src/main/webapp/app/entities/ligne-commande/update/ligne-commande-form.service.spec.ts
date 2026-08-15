import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../ligne-commande.test-samples';

import { LigneCommandeFormService } from './ligne-commande-form.service';

describe('LigneCommande Form Service', () => {
  let service: LigneCommandeFormService;

  beforeEach(() => {
    service = TestBed.inject(LigneCommandeFormService);
  });

  describe('Service methods', () => {
    describe('createLigneCommandeFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLigneCommandeFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            prixUnitaire: expect.any(Object),
            variante: expect.any(Object),
            commande: expect.any(Object),
          }),
        );
      });

      it('passing ILigneCommande should create a new form with FormGroup', () => {
        const formGroup = service.createLigneCommandeFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            quantite: expect.any(Object),
            prixUnitaire: expect.any(Object),
            variante: expect.any(Object),
            commande: expect.any(Object),
          }),
        );
      });
    });

    describe('getLigneCommande', () => {
      it('should return NewLigneCommande for default LigneCommande initial value', () => {
        const formGroup = service.createLigneCommandeFormGroup(sampleWithNewData);

        const ligneCommande = service.getLigneCommande(formGroup);

        expect(ligneCommande).toMatchObject(sampleWithNewData);
      });

      it('should return NewLigneCommande for empty LigneCommande initial value', () => {
        const formGroup = service.createLigneCommandeFormGroup();

        const ligneCommande = service.getLigneCommande(formGroup);

        expect(ligneCommande).toMatchObject({});
      });

      it('should return ILigneCommande', () => {
        const formGroup = service.createLigneCommandeFormGroup(sampleWithRequiredData);

        const ligneCommande = service.getLigneCommande(formGroup);

        expect(ligneCommande).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILigneCommande should not enable id FormControl', () => {
        const formGroup = service.createLigneCommandeFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLigneCommande should disable id FormControl', () => {
        const formGroup = service.createLigneCommandeFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
