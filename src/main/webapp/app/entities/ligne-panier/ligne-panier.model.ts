import { IPanier } from 'app/entities/panier/panier.model';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';

export interface ILignePanier {
  id: number;
  quantite?: number | null;
  variante?: Pick<IVarianteProduit, 'id'> | null;
  panier?: Pick<IPanier, 'id'> | null;
}

export type NewLignePanier = Omit<ILignePanier, 'id'> & { id: null };
