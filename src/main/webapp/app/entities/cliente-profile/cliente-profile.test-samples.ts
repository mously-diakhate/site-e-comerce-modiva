import { IClienteProfile, NewClienteProfile } from './cliente-profile.model';

export const sampleWithRequiredData: IClienteProfile = {
  id: 19723,
};

export const sampleWithPartialData: IClienteProfile = {
  id: 28086,
  telephone: '0504801941',
  actif: false,
};

export const sampleWithFullData: IClienteProfile = {
  id: 5980,
  telephone: '+33 476858346',
  actif: true,
};

export const sampleWithNewData: NewClienteProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
