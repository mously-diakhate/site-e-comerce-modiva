import { IPanier } from 'app/entities/panier/panier.model';
import { IUser } from 'app/entities/user/user.model';

export interface IClienteProfile {
  id: number;
  telephone?: string | null;
  actif?: boolean | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  panier?: Pick<IPanier, 'id'> | null;
}

export type NewClienteProfile = Omit<IClienteProfile, 'id'> & { id: null };
