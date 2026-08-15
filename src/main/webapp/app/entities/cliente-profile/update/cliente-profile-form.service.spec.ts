import { beforeEach, describe, expect, it } from 'vitest';
import { TestBed } from '@angular/core/testing';

import { sampleWithNewData, sampleWithRequiredData } from '../cliente-profile.test-samples';

import { ClienteProfileFormService } from './cliente-profile-form.service';

describe('ClienteProfile Form Service', () => {
  let service: ClienteProfileFormService;

  beforeEach(() => {
    service = TestBed.inject(ClienteProfileFormService);
  });

  describe('Service methods', () => {
    describe('createClienteProfileFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createClienteProfileFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            telephone: expect.any(Object),
            actif: expect.any(Object),
            user: expect.any(Object),
            panier: expect.any(Object),
          }),
        );
      });

      it('passing IClienteProfile should create a new form with FormGroup', () => {
        const formGroup = service.createClienteProfileFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            telephone: expect.any(Object),
            actif: expect.any(Object),
            user: expect.any(Object),
            panier: expect.any(Object),
          }),
        );
      });
    });

    describe('getClienteProfile', () => {
      it('should return NewClienteProfile for default ClienteProfile initial value', () => {
        const formGroup = service.createClienteProfileFormGroup(sampleWithNewData);

        const clienteProfile = service.getClienteProfile(formGroup);

        expect(clienteProfile).toMatchObject(sampleWithNewData);
      });

      it('should return NewClienteProfile for empty ClienteProfile initial value', () => {
        const formGroup = service.createClienteProfileFormGroup();

        const clienteProfile = service.getClienteProfile(formGroup);

        expect(clienteProfile).toMatchObject({});
      });

      it('should return IClienteProfile', () => {
        const formGroup = service.createClienteProfileFormGroup(sampleWithRequiredData);

        const clienteProfile = service.getClienteProfile(formGroup);

        expect(clienteProfile).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IClienteProfile should not enable id FormControl', () => {
        const formGroup = service.createClienteProfileFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewClienteProfile should disable id FormControl', () => {
        const formGroup = service.createClienteProfileFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
