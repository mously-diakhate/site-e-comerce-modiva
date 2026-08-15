import { IProduit, NewProduit } from './produit.model';

export const sampleWithRequiredData: IProduit = {
  id: 10819,
  nom: 'avoir agréable broum',
  prix: 7602.47,
  statut: 'INACTIF',
};

export const sampleWithPartialData: IProduit = {
  id: 15214,
  nom: 'résulter',
  description: '../fake-data/blob/hipster.txt',
  prix: 31921.15,
  statut: 'ACTIF',
};

export const sampleWithFullData: IProduit = {
  id: 2644,
  nom: 'en plus de comme aïe',
  description: '../fake-data/blob/hipster.txt',
  prix: 23324.46,
  statut: 'ACTIF',
};

export const sampleWithNewData: NewProduit = {
  nom: 'agréable loufoque',
  prix: 11078.62,
  statut: 'ACTIF',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
