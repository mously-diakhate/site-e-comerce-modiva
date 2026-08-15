import dayjs from 'dayjs/esm';

import { IClienteProfile } from 'app/entities/cliente-profile/cliente-profile.model';
import { StatutCommande } from 'app/entities/enumerations/statut-commande.model';
import { ILivraison } from 'app/entities/livraison/livraison.model';
import { IPaiement } from 'app/entities/paiement/paiement.model';

export interface ICommande {
  id: number;
  numero?: string | null;
  dateCommande?: dayjs.Dayjs | null;
  montantTotal?: number | null;
  modePaiement?: string | null;
  statutCommande?: keyof typeof StatutCommande | null;
  paiement?: Pick<IPaiement, 'id'> | null;
  livraison?: Pick<ILivraison, 'id'> | null;
  clienteProfile?: Pick<IClienteProfile, 'id'> | null;
}

export type NewCommande = Omit<ICommande, 'id'> & { id: null };
