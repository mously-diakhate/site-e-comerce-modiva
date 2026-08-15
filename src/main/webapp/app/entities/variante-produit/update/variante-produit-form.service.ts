import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IVarianteProduit, NewVarianteProduit } from '../variante-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IVarianteProduit for edit and NewVarianteProduitFormGroupInput for create.
 */
type VarianteProduitFormGroupInput = IVarianteProduit | PartialWithRequiredKeyOf<NewVarianteProduit>;

type VarianteProduitFormDefaults = Pick<NewVarianteProduit, 'id'>;

type VarianteProduitFormGroupContent = {
  id: FormControl<IVarianteProduit['id'] | NewVarianteProduit['id']>;
  stock: FormControl<IVarianteProduit['stock']>;
  taille: FormControl<IVarianteProduit['taille']>;
  couleur: FormControl<IVarianteProduit['couleur']>;
  produit: FormControl<IVarianteProduit['produit']>;
};

export type VarianteProduitFormGroup = FormGroup<VarianteProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class VarianteProduitFormService {
  createVarianteProduitFormGroup(varianteProduit?: VarianteProduitFormGroupInput): VarianteProduitFormGroup {
    const varianteProduitRawValue = {
      ...this.getFormDefaults(),
      ...(varianteProduit ?? { id: null }),
    };
    return new FormGroup<VarianteProduitFormGroupContent>({
      id: new FormControl(
        { value: varianteProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      stock: new FormControl(varianteProduitRawValue.stock, {
        validators: [Validators.required, Validators.min(0)],
      }),
      taille: new FormControl(varianteProduitRawValue.taille, {
        validators: [Validators.required],
      }),
      couleur: new FormControl(varianteProduitRawValue.couleur, {
        validators: [Validators.required],
      }),
      produit: new FormControl(varianteProduitRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getVarianteProduit(form: VarianteProduitFormGroup): IVarianteProduit | NewVarianteProduit {
    return form.getRawValue() as IVarianteProduit | NewVarianteProduit;
  }

  resetForm(form: VarianteProduitFormGroup, varianteProduit: VarianteProduitFormGroupInput): void {
    const varianteProduitRawValue = { ...this.getFormDefaults(), ...varianteProduit };
    form.reset({
      ...varianteProduitRawValue,
      id: { value: varianteProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): VarianteProduitFormDefaults {
    return {
      id: null,
    };
  }
}
