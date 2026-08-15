import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../image-produit.test-samples';

import { ImageProduitFormService } from './image-produit-form.service';

describe('ImageProduit Form Service', () => {
  let service: ImageProduitFormService;

  beforeEach(() => {
    service = TestBed.inject(ImageProduitFormService);
  });

  describe('Service methods', () => {
    describe('createImageProduitFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createImageProduitFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            url: expect.any(Object),
            ordre: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });

      it('passing IImageProduit should create a new form with FormGroup', () => {
        const formGroup = service.createImageProduitFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            url: expect.any(Object),
            ordre: expect.any(Object),
            produit: expect.any(Object),
          }),
        );
      });
    });

    describe('getImageProduit', () => {
      it('should return NewImageProduit for default ImageProduit initial value', () => {
        const formGroup = service.createImageProduitFormGroup(sampleWithNewData);

        const imageProduit = service.getImageProduit(formGroup);

        expect(imageProduit).toMatchObject(sampleWithNewData);
      });

      it('should return NewImageProduit for empty ImageProduit initial value', () => {
        const formGroup = service.createImageProduitFormGroup();

        const imageProduit = service.getImageProduit(formGroup);

        expect(imageProduit).toMatchObject({});
      });

      it('should return IImageProduit', () => {
        const formGroup = service.createImageProduitFormGroup(sampleWithRequiredData);

        const imageProduit = service.getImageProduit(formGroup);

        expect(imageProduit).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IImageProduit should not enable id FormControl', () => {
        const formGroup = service.createImageProduitFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewImageProduit should disable id FormControl', () => {
        const formGroup = service.createImageProduitFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
