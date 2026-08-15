import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ITaille, NewTaille } from '../taille.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ITaille for edit and NewTailleFormGroupInput for create.
 */
type TailleFormGroupInput = ITaille | PartialWithRequiredKeyOf<NewTaille>;

type TailleFormDefaults = Pick<NewTaille, 'id'>;

type TailleFormGroupContent = {
  id: FormControl<ITaille['id'] | NewTaille['id']>;
  libelle: FormControl<ITaille['libelle']>;
};

export type TailleFormGroup = FormGroup<TailleFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class TailleFormService {
  createTailleFormGroup(taille?: TailleFormGroupInput): TailleFormGroup {
    const tailleRawValue = {
      ...this.getFormDefaults(),
      ...(taille ?? { id: null }),
    };
    return new FormGroup<TailleFormGroupContent>({
      id: new FormControl(
        { value: tailleRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      libelle: new FormControl(tailleRawValue.libelle, {
        validators: [Validators.required],
      }),
    });
  }

  getTaille(form: TailleFormGroup): ITaille | NewTaille {
    return form.getRawValue() as ITaille | NewTaille;
  }

  resetForm(form: TailleFormGroup, taille: TailleFormGroupInput): void {
    const tailleRawValue = { ...this.getFormDefaults(), ...taille };
    form.reset({
      ...tailleRawValue,
      id: { value: tailleRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): TailleFormDefaults {
    return {
      id: null,
    };
  }
}
