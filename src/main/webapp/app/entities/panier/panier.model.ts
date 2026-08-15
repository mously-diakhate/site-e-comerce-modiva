import dayjs from 'dayjs/esm';

export interface IPanier {
  id: number;
  dateCreation?: dayjs.Dayjs | null;
}

export type NewPanier = Omit<IPanier, 'id'> & { id: null };
