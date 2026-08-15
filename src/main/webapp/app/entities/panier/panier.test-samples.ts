import dayjs from 'dayjs/esm';

import { IPanier, NewPanier } from './panier.model';

export const sampleWithRequiredData: IPanier = {
  id: 18426,
};

export const sampleWithPartialData: IPanier = {
  id: 20575,
};

export const sampleWithFullData: IPanier = {
  id: 8126,
  dateCreation: dayjs('2026-08-14T16:58'),
};

export const sampleWithNewData: NewPanier = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
