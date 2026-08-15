import dayjs from 'dayjs/esm';

import { ICommande, NewCommande } from './commande.model';

export const sampleWithRequiredData: ICommande = {
  id: 3263,
  numero: 'population du Québec hors bien que',
  dateCommande: dayjs('2026-08-14T03:20'),
  montantTotal: 27186.25,
  statutCommande: 'ANNULEE',
};

export const sampleWithPartialData: ICommande = {
  id: 21835,
  numero: 'circulaire à bas de',
  dateCommande: dayjs('2026-08-14T21:51'),
  montantTotal: 26520.46,
  modePaiement: 'administration prestataire de services',
  statutCommande: 'EN_ATTENTE',
};

export const sampleWithFullData: ICommande = {
  id: 32566,
  numero: 'plouf souhaiter',
  dateCommande: dayjs('2026-08-14T13:02'),
  montantTotal: 5527.55,
  modePaiement: 'tsoin-tsoin ouah rédaction',
  statutCommande: 'LIVREE',
};

export const sampleWithNewData: NewCommande = {
  numero: 'camarade tant que',
  dateCommande: dayjs('2026-08-14T21:57'),
  montantTotal: 6779.04,
  statutCommande: 'EN_ATTENTE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
