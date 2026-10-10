import { ICategorie } from 'app/entities/categorie/categorie.model';
import { StatutProduit } from 'app/entities/enumerations/statut-produit.model';
import { IPromotion } from 'app/entities/promotion/promotion.model';

export interface IProduit {
  id: number;
  nom?: string | null;
  description?: string | null;
  prix?: number | null;
  statut?: keyof typeof StatutProduit | null;
  promotions?: Pick<IPromotion, 'id' | 'nom'>[] | null;
  categorie?: Pick<ICategorie, 'id' | 'nom'> | null;
}

export type NewProduit = Omit<IProduit, 'id'> & { id: null };
