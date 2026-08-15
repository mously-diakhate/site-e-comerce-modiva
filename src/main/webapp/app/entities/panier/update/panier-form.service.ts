import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPanier, NewPanier } from '../panier.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPanier for edit and NewPanierFormGroupInput for create.
 */
type PanierFormGroupInput = IPanier | PartialWithRequiredKeyOf<NewPanier>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPanier | NewPanier> = Omit<T, 'dateCreation'> & {
  dateCreation?: string | null;
};

type PanierFormRawValue = FormValueOf<IPanier>;

type NewPanierFormRawValue = FormValueOf<NewPanier>;

type PanierFormDefaults = Pick<NewPanier, 'id' | 'dateCreation'>;

type PanierFormGroupContent = {
  id: FormControl<PanierFormRawValue['id'] | NewPanier['id']>;
  dateCreation: FormControl<PanierFormRawValue['dateCreation']>;
};

export type PanierFormGroup = FormGroup<PanierFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PanierFormService {
  createPanierFormGroup(panier?: PanierFormGroupInput): PanierFormGroup {
    const panierRawValue = this.convertPanierToPanierRawValue({
      ...this.getFormDefaults(),
      ...(panier ?? { id: null }),
    });
    return new FormGroup<PanierFormGroupContent>({
      id: new FormControl(
        { value: panierRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      dateCreation: new FormControl(panierRawValue.dateCreation),
    });
  }

  getPanier(form: PanierFormGroup): IPanier | NewPanier {
    return this.convertPanierRawValueToPanier(form.getRawValue() as PanierFormRawValue | NewPanierFormRawValue);
  }

  resetForm(form: PanierFormGroup, panier: PanierFormGroupInput): void {
    const panierRawValue = this.convertPanierToPanierRawValue({ ...this.getFormDefaults(), ...panier });
    form.reset({
      ...panierRawValue,
      id: { value: panierRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PanierFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateCreation: currentTime,
    };
  }

  private convertPanierRawValueToPanier(rawPanier: PanierFormRawValue | NewPanierFormRawValue): IPanier | NewPanier {
    return {
      ...rawPanier,
      dateCreation: dayjs(rawPanier.dateCreation, DATE_TIME_FORMAT),
    };
  }

  private convertPanierToPanierRawValue(
    panier: IPanier | (Partial<NewPanier> & PanierFormDefaults),
  ): PanierFormRawValue | PartialWithRequiredKeyOf<NewPanierFormRawValue> {
    return {
      ...panier,
      dateCreation: panier.dateCreation ? panier.dateCreation.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
