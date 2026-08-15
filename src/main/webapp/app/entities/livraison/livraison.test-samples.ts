import { ILivraison, NewLivraison } from './livraison.model';

export const sampleWithRequiredData: ILivraison = {
  id: 7706,
  adresse: 'hôte',
  ville: 'habile',
  statutLivraison: 'EN_ATTENTE',
};

export const sampleWithPartialData: ILivraison = {
  id: 3541,
  adresse: 'rédaction en placer',
  ville: 'enterrer bof brave',
  quartier: 'clientèle',
  statutLivraison: 'EXPEDIEE',
};

export const sampleWithFullData: ILivraison = {
  id: 26803,
  adresse: 'trop comme',
  ville: 'ha ha espiègle',
  quartier: 'autour de occuper conseil municipal',
  telephone: '0667795618',
  fraisLivraison: 18088.29,
  statutLivraison: 'EN_COURS',
};

export const sampleWithNewData: NewLivraison = {
  adresse: 'simple sauvage',
  ville: 'lors sitôt que',
  statutLivraison: 'EN_ATTENTE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
