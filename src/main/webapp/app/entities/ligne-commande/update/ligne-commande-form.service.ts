import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILigneCommande, NewLigneCommande } from '../ligne-commande.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILigneCommande for edit and NewLigneCommandeFormGroupInput for create.
 */
type LigneCommandeFormGroupInput = ILigneCommande | PartialWithRequiredKeyOf<NewLigneCommande>;

type LigneCommandeFormDefaults = Pick<NewLigneCommande, 'id'>;

type LigneCommandeFormGroupContent = {
  id: FormControl<ILigneCommande['id'] | NewLigneCommande['id']>;
  quantite: FormControl<ILigneCommande['quantite']>;
  prixUnitaire: FormControl<ILigneCommande['prixUnitaire']>;
  variante: FormControl<ILigneCommande['variante']>;
  commande: FormControl<ILigneCommande['commande']>;
};

export type LigneCommandeFormGroup = FormGroup<LigneCommandeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LigneCommandeFormService {
  createLigneCommandeFormGroup(ligneCommande?: LigneCommandeFormGroupInput): LigneCommandeFormGroup {
    const ligneCommandeRawValue = {
      ...this.getFormDefaults(),
      ...(ligneCommande ?? { id: null }),
    };
    return new FormGroup<LigneCommandeFormGroupContent>({
      id: new FormControl(
        { value: ligneCommandeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantite: new FormControl(ligneCommandeRawValue.quantite, {
        validators: [Validators.required, Validators.min(1)],
      }),
      prixUnitaire: new FormControl(ligneCommandeRawValue.prixUnitaire, {
        validators: [Validators.required],
      }),
      variante: new FormControl(ligneCommandeRawValue.variante, {
        validators: [Validators.required],
      }),
      commande: new FormControl(ligneCommandeRawValue.commande, {
        validators: [Validators.required],
      }),
    });
  }

  getLigneCommande(form: LigneCommandeFormGroup): ILigneCommande | NewLigneCommande {
    return form.getRawValue() as ILigneCommande | NewLigneCommande;
  }

  resetForm(form: LigneCommandeFormGroup, ligneCommande: LigneCommandeFormGroupInput): void {
    const ligneCommandeRawValue = { ...this.getFormDefaults(), ...ligneCommande };
    form.reset({
      ...ligneCommandeRawValue,
      id: { value: ligneCommandeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LigneCommandeFormDefaults {
    return {
      id: null,
    };
  }
}
