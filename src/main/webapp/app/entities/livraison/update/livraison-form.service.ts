import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { ILivraison, NewLivraison } from '../livraison.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ILivraison for edit and NewLivraisonFormGroupInput for create.
 */
type LivraisonFormGroupInput = ILivraison | PartialWithRequiredKeyOf<NewLivraison>;

type LivraisonFormDefaults = Pick<NewLivraison, 'id'>;

type LivraisonFormGroupContent = {
  id: FormControl<ILivraison['id'] | NewLivraison['id']>;
  adresse: FormControl<ILivraison['adresse']>;
  ville: FormControl<ILivraison['ville']>;
  quartier: FormControl<ILivraison['quartier']>;
  telephone: FormControl<ILivraison['telephone']>;
  fraisLivraison: FormControl<ILivraison['fraisLivraison']>;
  statutLivraison: FormControl<ILivraison['statutLivraison']>;
};

export type LivraisonFormGroup = FormGroup<LivraisonFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class LivraisonFormService {
  createLivraisonFormGroup(livraison?: LivraisonFormGroupInput): LivraisonFormGroup {
    const livraisonRawValue = {
      ...this.getFormDefaults(),
      ...(livraison ?? { id: null }),
    };
    return new FormGroup<LivraisonFormGroupContent>({
      id: new FormControl(
        { value: livraisonRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      adresse: new FormControl(livraisonRawValue.adresse, {
        validators: [Validators.required],
      }),
      ville: new FormControl(livraisonRawValue.ville, {
        validators: [Validators.required],
      }),
      quartier: new FormControl(livraisonRawValue.quartier),
      telephone: new FormControl(livraisonRawValue.telephone),
      fraisLivraison: new FormControl(livraisonRawValue.fraisLivraison),
      statutLivraison: new FormControl(livraisonRawValue.statutLivraison, {
        validators: [Validators.required],
      }),
    });
  }

  getLivraison(form: LivraisonFormGroup): ILivraison | NewLivraison {
    return form.getRawValue() as ILivraison | NewLivraison;
  }

  resetForm(form: LivraisonFormGroup, livraison: LivraisonFormGroupInput): void {
    const livraisonRawValue = { ...this.getFormDefaults(), ...livraison };
    form.reset({
      ...livraisonRawValue,
      id: { value: livraisonRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): LivraisonFormDefaults {
    return {
      id: null,
    };
  }
}
