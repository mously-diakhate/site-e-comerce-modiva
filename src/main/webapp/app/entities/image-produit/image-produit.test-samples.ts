import { IImageProduit, NewImageProduit } from './image-produit.model';

export const sampleWithRequiredData: IImageProduit = {
  id: 16605,
  url: 'https://intrepide-commissionnaire.info',
};

export const sampleWithPartialData: IImageProduit = {
  id: 14117,
  url: 'https://energique-secouriste.info',
};

export const sampleWithFullData: IImageProduit = {
  id: 29391,
  url: 'https://gai-conseil-municipal.info/',
  ordre: 32335,
};

export const sampleWithNewData: NewImageProduit = {
  url: 'https://insipide-delegation.com',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
