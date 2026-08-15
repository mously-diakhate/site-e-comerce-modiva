import { ILigneCommande, NewLigneCommande } from './ligne-commande.model';

export const sampleWithRequiredData: ILigneCommande = {
  id: 14102,
  quantite: 18123,
  prixUnitaire: 6522.74,
};

export const sampleWithPartialData: ILigneCommande = {
  id: 11204,
  quantite: 17158,
  prixUnitaire: 5249.09,
};

export const sampleWithFullData: ILigneCommande = {
  id: 12600,
  quantite: 25773,
  prixUnitaire: 7669.18,
};

export const sampleWithNewData: NewLigneCommande = {
  quantite: 29384,
  prixUnitaire: 29024.53,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
