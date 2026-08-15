import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IClienteProfile, NewClienteProfile } from '../cliente-profile.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IClienteProfile for edit and NewClienteProfileFormGroupInput for create.
 */
type ClienteProfileFormGroupInput = IClienteProfile | PartialWithRequiredKeyOf<NewClienteProfile>;

type ClienteProfileFormDefaults = Pick<NewClienteProfile, 'id' | 'actif'>;

type ClienteProfileFormGroupContent = {
  id: FormControl<IClienteProfile['id'] | NewClienteProfile['id']>;
  telephone: FormControl<IClienteProfile['telephone']>;
  actif: FormControl<IClienteProfile['actif']>;
  user: FormControl<IClienteProfile['user']>;
  panier: FormControl<IClienteProfile['panier']>;
};

export type ClienteProfileFormGroup = FormGroup<ClienteProfileFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ClienteProfileFormService {
  createClienteProfileFormGroup(clienteProfile?: ClienteProfileFormGroupInput): ClienteProfileFormGroup {
    const clienteProfileRawValue = {
      ...this.getFormDefaults(),
      ...(clienteProfile ?? { id: null }),
    };
    return new FormGroup<ClienteProfileFormGroupContent>({
      id: new FormControl(
        { value: clienteProfileRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      telephone: new FormControl(clienteProfileRawValue.telephone),
      actif: new FormControl(clienteProfileRawValue.actif),
      user: new FormControl(clienteProfileRawValue.user, {
        validators: [Validators.required],
      }),
      panier: new FormControl(clienteProfileRawValue.panier),
    });
  }

  getClienteProfile(form: ClienteProfileFormGroup): IClienteProfile | NewClienteProfile {
    return form.getRawValue() as IClienteProfile | NewClienteProfile;
  }

  resetForm(form: ClienteProfileFormGroup, clienteProfile: ClienteProfileFormGroupInput): void {
    const clienteProfileRawValue = { ...this.getFormDefaults(), ...clienteProfile };
    form.reset({
      ...clienteProfileRawValue,
      id: { value: clienteProfileRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ClienteProfileFormDefaults {
    return {
      id: null,
      actif: false,
    };
  }
}
