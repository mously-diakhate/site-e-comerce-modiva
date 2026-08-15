import dayjs from 'dayjs/esm';

import { IPromotion, NewPromotion } from './promotion.model';

export const sampleWithRequiredData: IPromotion = {
  id: 12646,
  nom: 'brusque glouglou',
  reduction: 19097.34,
  dateDebut: dayjs('2026-08-14T06:13'),
  dateFin: dayjs('2026-08-14T13:37'),
};

export const sampleWithPartialData: IPromotion = {
  id: 7418,
  nom: 'participer',
  reduction: 29699.19,
  dateDebut: dayjs('2026-08-14T08:09'),
  dateFin: dayjs('2026-08-14T23:04'),
};

export const sampleWithFullData: IPromotion = {
  id: 16704,
  nom: 'tic-tac',
  reduction: 24702,
  dateDebut: dayjs('2026-08-14T09:09'),
  dateFin: dayjs('2026-08-14T21:22'),
};

export const sampleWithNewData: NewPromotion = {
  nom: 'parfois direction',
  reduction: 27722.34,
  dateDebut: dayjs('2026-08-14T08:35'),
  dateFin: dayjs('2026-08-14T15:10'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
