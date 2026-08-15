export interface ITaille {
  id: number;
  libelle?: string | null;
}

export type NewTaille = Omit<ITaille, 'id'> & { id: null };
