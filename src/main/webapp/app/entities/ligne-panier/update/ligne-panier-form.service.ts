import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILignePanier, NewLignePanier } from '../ligne-panier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILignePanier for edit and NewLignePanierFormGroupInput for create.
 */
type LignePanierFormGroupInput = ILignePanier | PartialWithRequiredKeyOf<NewLignePanier>;

type LignePanierFormDefaults = Pick<NewLignePanier, 'id'>;

type LignePanierFormGroupContent = {
  id: FormControl<ILignePanier['id'] | NewLignePanier['id']>;
  quantite: FormControl<ILignePanier['quantite']>;
  variante: FormControl<ILignePanier['variante']>;
  panier: FormControl<ILignePanier['panier']>;
};

export type LignePanierFormGroup = FormGroup<LignePanierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LignePanierFormService {
  createLignePanierFormGroup(lignePanier?: LignePanierFormGroupInput): LignePanierFormGroup {
    const lignePanierRawValue = {
      ...this.getFormDefaults(),
      ...(lignePanier ?? { id: null }),
    };
    return new FormGroup<LignePanierFormGroupContent>({
      id: new FormControl(
        { value: lignePanierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      quantite: new FormControl(lignePanierRawValue.quantite, {
        validators: [Validators.required, Validators.min(1)],
      }),
      variante: new FormControl(lignePanierRawValue.variante, {
        validators: [Validators.required],
      }),
      panier: new FormControl(lignePanierRawValue.panier, {
        validators: [Validators.required],
      }),
    });
  }

  getLignePanier(form: LignePanierFormGroup): ILignePanier | NewLignePanier {
    return form.getRawValue() as ILignePanier | NewLignePanier;
  }

  resetForm(form: LignePanierFormGroup, lignePanier: LignePanierFormGroupInput): void {
    const lignePanierRawValue = { ...this.getFormDefaults(), ...lignePanier };
    form.reset({
      ...lignePanierRawValue,
      id: { value: lignePanierRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LignePanierFormDefaults {
    return {
      id: null,
    };
  }
}
