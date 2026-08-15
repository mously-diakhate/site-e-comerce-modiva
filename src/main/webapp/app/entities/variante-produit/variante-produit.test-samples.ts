import { IVarianteProduit, NewVarianteProduit } from './variante-produit.model';

export const sampleWithRequiredData: IVarianteProduit = {
  id: 4807,
  stock: 4482,
};

export const sampleWithPartialData: IVarianteProduit = {
  id: 16995,
  stock: 13787,
};

export const sampleWithFullData: IVarianteProduit = {
  id: 26570,
  stock: 26907,
};

export const sampleWithNewData: NewVarianteProduit = {
  stock: 13240,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
