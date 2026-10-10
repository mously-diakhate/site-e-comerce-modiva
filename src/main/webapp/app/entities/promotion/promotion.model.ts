import dayjs from 'dayjs/esm';

import { IProduit } from 'app/entities/produit/produit.model';

export interface IPromotion {
  id: number;
  nom?: string | null;
  reduction?: number | null;
  dateDebut?: dayjs.Dayjs | null;
  dateFin?: dayjs.Dayjs | null;
  produits?: Pick<IProduit, 'id' | 'nom'>[] | null;
}

export type NewPromotion = Omit<IPromotion, 'id'> & { id: null };
