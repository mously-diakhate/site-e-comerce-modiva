import { ICouleur } from 'app/entities/couleur/couleur.model';
import { IProduit } from 'app/entities/produit/produit.model';
import { ITaille } from 'app/entities/taille/taille.model';

export interface IVarianteProduit {
  id: number;
  stock?: number | null;
  taille?: Pick<ITaille, 'id'> | null;
  couleur?: Pick<ICouleur, 'id'> | null;
  produit?: Pick<IProduit, 'id'> | null;
}

export type NewVarianteProduit = Omit<IVarianteProduit, 'id'> & { id: null };
