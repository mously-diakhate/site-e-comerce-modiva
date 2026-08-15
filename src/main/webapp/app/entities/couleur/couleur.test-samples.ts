import { ICouleur, NewCouleur } from './couleur.model';

export const sampleWithRequiredData: ICouleur = {
  id: 16596,
  libelle: 'actionnaire prestataire de services',
};

export const sampleWithPartialData: ICouleur = {
  id: 29790,
  libelle: 'équipe',
};

export const sampleWithFullData: ICouleur = {
  id: 2894,
  libelle: 'hypocrite déclencher',
  codeHex: 'exprès',
};

export const sampleWithNewData: NewCouleur = {
  libelle: "chef de cuisine proche de à l'instar de",
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
