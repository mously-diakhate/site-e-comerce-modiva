import dayjs from 'dayjs/esm';

import { StatutPaiement } from 'app/entities/enumerations/statut-paiement.model';

export interface IPaiement {
  id: number;
  montant?: number | null;
  datePaiement?: dayjs.Dayjs | null;
  moyenPaiement?: string | null;
  referenceTransaction?: string | null;
  statutPaiement?: keyof typeof StatutPaiement | null;
}

export type NewPaiement = Omit<IPaiement, 'id'> & { id: null };
