import dayjs from 'dayjs/esm';

import { IPaiement, NewPaiement } from './paiement.model';

export const sampleWithRequiredData: IPaiement = {
  id: 5155,
  montant: 31009.05,
  statutPaiement: 'REMBOURSE',
};

export const sampleWithPartialData: IPaiement = {
  id: 11912,
  montant: 12773.72,
  referenceTransaction: 'tic-tac jusqu’à ce que',
  statutPaiement: 'REMBOURSE',
};

export const sampleWithFullData: IPaiement = {
  id: 8664,
  montant: 4831.51,
  datePaiement: dayjs('2026-08-14T17:51'),
  moyenPaiement: 'population du Québec',
  referenceTransaction: 'premièrement',
  statutPaiement: 'REUSSI',
};

export const sampleWithNewData: NewPaiement = {
  montant: 30623.7,
  statutPaiement: 'EN_ATTENTE',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
