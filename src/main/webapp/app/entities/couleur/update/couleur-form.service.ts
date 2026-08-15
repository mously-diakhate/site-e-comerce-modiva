import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ICouleur, NewCouleur } from '../couleur.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICouleur for edit and NewCouleurFormGroupInput for create.
 */
type CouleurFormGroupInput = ICouleur | PartialWithRequiredKeyOf<NewCouleur>;

type CouleurFormDefaults = Pick<NewCouleur, 'id'>;

type CouleurFormGroupContent = {
  id: FormControl<ICouleur['id'] | NewCouleur['id']>;
  libelle: FormControl<ICouleur['libelle']>;
  codeHex: FormControl<ICouleur['codeHex']>;
};

export type CouleurFormGroup = FormGroup<CouleurFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CouleurFormService {
  createCouleurFormGroup(couleur?: CouleurFormGroupInput): CouleurFormGroup {
    const couleurRawValue = {
      ...this.getFormDefaults(),
      ...(couleur ?? { id: null }),
    };
    return new FormGroup<CouleurFormGroupContent>({
      id: new FormControl(
        { value: couleurRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      libelle: new FormControl(couleurRawValue.libelle, {
        validators: [Validators.required],
      }),
      codeHex: new FormControl(couleurRawValue.codeHex),
    });
  }

  getCouleur(form: CouleurFormGroup): ICouleur | NewCouleur {
    return form.getRawValue() as ICouleur | NewCouleur;
  }

  resetForm(form: CouleurFormGroup, couleur: CouleurFormGroupInput): void {
    const couleurRawValue = { ...this.getFormDefaults(), ...couleur };
    form.reset({
      ...couleurRawValue,
      id: { value: couleurRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CouleurFormDefaults {
    return {
      id: null,
    };
  }
}
