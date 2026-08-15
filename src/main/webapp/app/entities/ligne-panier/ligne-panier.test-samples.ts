import { ILignePanier, NewLignePanier } from './ligne-panier.model';

export const sampleWithRequiredData: ILignePanier = {
  id: 26422,
  quantite: 20680,
};

export const sampleWithPartialData: ILignePanier = {
  id: 24331,
  quantite: 8398,
};

export const sampleWithFullData: ILignePanier = {
  id: 8952,
  quantite: 30167,
};

export const sampleWithNewData: NewLignePanier = {
  quantite: 20094,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
