import { StatutLivraison } from 'app/entities/enumerations/statut-livraison.model';

export interface ILivraison {
  id: number;
  adresse?: string | null;
  ville?: string | null;
  quartier?: string | null;
  telephone?: string | null;
  fraisLivraison?: number | null;
  statutLivraison?: keyof typeof StatutLivraison | null;
}

export type NewLivraison = Omit<ILivraison, 'id'> & { id: null };
