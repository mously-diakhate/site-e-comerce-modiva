export interface ICouleur {
  id: number;
  libelle?: string | null;
  codeHex?: string | null;
}

export type NewCouleur = Omit<ICouleur, 'id'> & { id: null };
