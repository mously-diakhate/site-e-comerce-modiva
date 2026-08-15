import { IProduit } from 'app/entities/produit/produit.model';

export interface IImageProduit {
  id: number;
  url?: string | null;
  ordre?: number | null;
  produit?: Pick<IProduit, 'id'> | null;
}

export type NewImageProduit = Omit<IImageProduit, 'id'> & { id: null };
