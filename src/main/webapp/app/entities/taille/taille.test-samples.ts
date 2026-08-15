import { ITaille, NewTaille } from './taille.model';

export const sampleWithRequiredData: ITaille = {
  id: 130,
  libelle: 'au-devant',
};

export const sampleWithPartialData: ITaille = {
  id: 9220,
  libelle: 'commis hé',
};

export const sampleWithFullData: ITaille = {
  id: 19724,
  libelle: 'en dépit de tard en vérité',
};

export const sampleWithNewData: NewTaille = {
  libelle: 'nonobstant devant',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
