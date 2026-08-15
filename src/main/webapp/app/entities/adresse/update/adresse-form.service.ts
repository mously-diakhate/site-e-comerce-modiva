import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IAdresse, NewAdresse } from '../adresse.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IAdresse for edit and NewAdresseFormGroupInput for create.
 */
type AdresseFormGroupInput = IAdresse | PartialWithRequiredKeyOf<NewAdresse>;

type AdresseFormDefaults = Pick<NewAdresse, 'id' | 'parDefaut'>;

type AdresseFormGroupContent = {
  id: FormControl<IAdresse['id'] | NewAdresse['id']>;
  libelle: FormControl<IAdresse['libelle']>;
  adresse: FormControl<IAdresse['adresse']>;
  ville: FormControl<IAdresse['ville']>;
  quartier: FormControl<IAdresse['quartier']>;
  telephone: FormControl<IAdresse['telephone']>;
  parDefaut: FormControl<IAdresse['parDefaut']>;
  clienteProfile: FormControl<IAdresse['clienteProfile']>;
};

export type AdresseFormGroup = FormGroup<AdresseFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class AdresseFormService {
  createAdresseFormGroup(adresse?: AdresseFormGroupInput): AdresseFormGroup {
    const adresseRawValue = {
      ...this.getFormDefaults(),
      ...(adresse ?? { id: null }),
    };
    return new FormGroup<AdresseFormGroupContent>({
      id: new FormControl(
        { value: adresseRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      libelle: new FormControl(adresseRawValue.libelle),
      adresse: new FormControl(adresseRawValue.adresse, {
        validators: [Validators.required],
      }),
      ville: new FormControl(adresseRawValue.ville, {
        validators: [Validators.required],
      }),
      quartier: new FormControl(adresseRawValue.quartier),
      telephone: new FormControl(adresseRawValue.telephone),
      parDefaut: new FormControl(adresseRawValue.parDefaut),
      clienteProfile: new FormControl(adresseRawValue.clienteProfile, {
        validators: [Validators.required],
      }),
    });
  }

  getAdresse(form: AdresseFormGroup): IAdresse | NewAdresse {
    return form.getRawValue() as IAdresse | NewAdresse;
  }

  resetForm(form: AdresseFormGroup, adresse: AdresseFormGroupInput): void {
    const adresseRawValue = { ...this.getFormDefaults(), ...adresse };
    form.reset({
      ...adresseRawValue,
      id: { value: adresseRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): AdresseFormDefaults {
    return {
      id: null,
      parDefaut: false,
    };
  }
}
