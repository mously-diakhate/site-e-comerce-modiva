import { IAdresse, NewAdresse } from './adresse.model';

export const sampleWithRequiredData: IAdresse = {
  id: 11524,
  adresse: 'rapide broum',
  ville: 'avant',
};

export const sampleWithPartialData: IAdresse = {
  id: 13533,
  libelle: 'sans que a',
  adresse: 'antique agréable pointer',
  ville: 'projeter commis de cuisine rédaction',
  quartier: 'en vérité ouch pff',
};

export const sampleWithFullData: IAdresse = {
  id: 31736,
  libelle: 'selon croâ',
  adresse: 'habile paf',
  ville: "à l'insu de en dehors de candide",
  quartier: "d'avec",
  telephone: '+33 186166462',
  parDefaut: true,
};

export const sampleWithNewData: NewAdresse = {
  adresse: 'splendide',
  ville: 'sage membre à vie',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
