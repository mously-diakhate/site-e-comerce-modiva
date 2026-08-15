import { IClienteProfile } from 'app/entities/cliente-profile/cliente-profile.model';

export interface IAdresse {
  id: number;
  libelle?: string | null;
  adresse?: string | null;
  ville?: string | null;
  quartier?: string | null;
  telephone?: string | null;
  parDefaut?: boolean | null;
  clienteProfile?: Pick<IClienteProfile, 'id'> | null;
}

export type NewAdresse = Omit<IAdresse, 'id'> & { id: null };
