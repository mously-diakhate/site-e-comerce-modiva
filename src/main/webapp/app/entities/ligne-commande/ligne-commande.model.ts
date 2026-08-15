import { ICommande } from 'app/entities/commande/commande.model';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';

export interface ILigneCommande {
  id: number;
  quantite?: number | null;
  prixUnitaire?: number | null;
  variante?: Pick<IVarianteProduit, 'id'> | null;
  commande?: Pick<ICommande, 'id'> | null;
}

export type NewLigneCommande = Omit<ILigneCommande, 'id'> & { id: null };
