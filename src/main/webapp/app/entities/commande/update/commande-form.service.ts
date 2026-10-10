import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICommande, NewCommande } from '../commande.model';

type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

type CommandeFormGroupInput = ICommande | PartialWithRequiredKeyOf<NewCommande>;

type FormValueOf<T extends ICommande | NewCommande> = Omit<T, 'dateCommande'> & {
  dateCommande?: string | null;
};

type CommandeFormRawValue = FormValueOf<ICommande>;

type NewCommandeFormRawValue = FormValueOf<NewCommande>;

type CommandeFormDefaults = Pick<NewCommande, 'id' | 'dateCommande'>;

type CommandeFormGroupContent = {
  id: FormControl<CommandeFormRawValue['id'] | NewCommande['id']>;
  numero: FormControl<CommandeFormRawValue['numero']>;
  dateCommande: FormControl<CommandeFormRawValue['dateCommande']>;
  montantTotal: FormControl<CommandeFormRawValue['montantTotal']>;
  modePaiement: FormControl<CommandeFormRawValue['modePaiement']>;
  statutCommande: FormControl<CommandeFormRawValue['statutCommande']>;
  clienteProfile: FormControl<CommandeFormRawValue['clienteProfile']>;
};

export type CommandeFormGroup = FormGroup<CommandeFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CommandeFormService {
  createCommandeFormGroup(commande?: CommandeFormGroupInput): CommandeFormGroup {
    const commandeRawValue = this.convertCommandeToCommandeRawValue({
      ...this.getFormDefaults(),
      ...(commande ?? { id: null }),
    });
    return new FormGroup<CommandeFormGroupContent>({
      id: new FormControl(
        { value: commandeRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      numero: new FormControl(commandeRawValue.numero, {
        validators: [Validators.required],
      }),
      dateCommande: new FormControl(commandeRawValue.dateCommande, {
        validators: [Validators.required],
      }),
      montantTotal: new FormControl(commandeRawValue.montantTotal, {
        validators: [Validators.required],
      }),
      modePaiement: new FormControl(commandeRawValue.modePaiement),
      statutCommande: new FormControl(commandeRawValue.statutCommande, {
        validators: [Validators.required],
      }),
      clienteProfile: new FormControl(commandeRawValue.clienteProfile, {
        validators: [Validators.required],
      }),
    });
  }

  getCommande(form: CommandeFormGroup): ICommande | NewCommande {
    return this.convertCommandeRawValueToCommande(form.getRawValue() as CommandeFormRawValue | NewCommandeFormRawValue);
  }

  resetForm(form: CommandeFormGroup, commande: CommandeFormGroupInput): void {
    const commandeRawValue = this.convertCommandeToCommandeRawValue({ ...this.getFormDefaults(), ...commande });
    form.reset({
      ...commandeRawValue,
      id: { value: commandeRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): CommandeFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCommande: currentTime,
    };
  }

  private convertCommandeRawValueToCommande(rawCommande: CommandeFormRawValue | NewCommandeFormRawValue): ICommande | NewCommande {
    return {
      ...rawCommande,
      dateCommande: dayjs(rawCommande.dateCommande, DATE_TIME_FORMAT),
    };
  }

  private convertCommandeToCommandeRawValue(
    commande: ICommande | (Partial<NewCommande> & CommandeFormDefaults),
  ): CommandeFormRawValue | PartialWithRequiredKeyOf<NewCommandeFormRawValue> {
    return {
      ...commande,
      dateCommande: commande.dateCommande ? commande.dateCommande.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
