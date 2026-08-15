import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPromotion, NewPromotion } from '../promotion.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPromotion for edit and NewPromotionFormGroupInput for create.
 */
type PromotionFormGroupInput = IPromotion | PartialWithRequiredKeyOf<NewPromotion>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IPromotion | NewPromotion> = Omit<T, 'dateDebut' | 'dateFin'> & {
  dateDebut?: string | null;
  dateFin?: string | null;
};

type PromotionFormRawValue = FormValueOf<IPromotion>;

type NewPromotionFormRawValue = FormValueOf<NewPromotion>;

type PromotionFormDefaults = Pick<NewPromotion, 'id' | 'dateDebut' | 'dateFin' | 'produits'>;

type PromotionFormGroupContent = {
  id: FormControl<PromotionFormRawValue['id'] | NewPromotion['id']>;
  nom: FormControl<PromotionFormRawValue['nom']>;
  reduction: FormControl<PromotionFormRawValue['reduction']>;
  dateDebut: FormControl<PromotionFormRawValue['dateDebut']>;
  dateFin: FormControl<PromotionFormRawValue['dateFin']>;
  produits: FormControl<PromotionFormRawValue['produits']>;
};

export type PromotionFormGroup = FormGroup<PromotionFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PromotionFormService {
  createPromotionFormGroup(promotion?: PromotionFormGroupInput): PromotionFormGroup {
    const promotionRawValue = this.convertPromotionToPromotionRawValue({
      ...this.getFormDefaults(),
      ...(promotion ?? { id: null }),
    });
    return new FormGroup<PromotionFormGroupContent>({
      id: new FormControl(
        { value: promotionRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(promotionRawValue.nom, {
        validators: [Validators.required],
      }),
      reduction: new FormControl(promotionRawValue.reduction, {
        validators: [Validators.required, Validators.min(0)],
      }),
      dateDebut: new FormControl(promotionRawValue.dateDebut, {
        validators: [Validators.required],
      }),
      dateFin: new FormControl(promotionRawValue.dateFin, {
        validators: [Validators.required],
      }),
      produits: new FormControl(promotionRawValue.produits ?? []),
    });
  }

  getPromotion(form: PromotionFormGroup): IPromotion | NewPromotion {
    return this.convertPromotionRawValueToPromotion(form.getRawValue() as PromotionFormRawValue | NewPromotionFormRawValue);
  }

  resetForm(form: PromotionFormGroup, promotion: PromotionFormGroupInput): void {
    const promotionRawValue = this.convertPromotionToPromotionRawValue({ ...this.getFormDefaults(), ...promotion });
    form.reset({
      ...promotionRawValue,
      id: { value: promotionRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): PromotionFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      dateDebut: currentTime,
      dateFin: currentTime,
      produits: [],
    };
  }

  private convertPromotionRawValueToPromotion(rawPromotion: PromotionFormRawValue | NewPromotionFormRawValue): IPromotion | NewPromotion {
    return {
      ...rawPromotion,
      dateDebut: dayjs(rawPromotion.dateDebut, DATE_TIME_FORMAT),
      dateFin: dayjs(rawPromotion.dateFin, DATE_TIME_FORMAT),
    };
  }

  private convertPromotionToPromotionRawValue(
    promotion: IPromotion | (Partial<NewPromotion> & PromotionFormDefaults),
  ): PromotionFormRawValue | PartialWithRequiredKeyOf<NewPromotionFormRawValue> {
    return {
      ...promotion,
      dateDebut: promotion.dateDebut ? promotion.dateDebut.format(DATE_TIME_FORMAT) : undefined,
      dateFin: promotion.dateFin ? promotion.dateFin.format(DATE_TIME_FORMAT) : undefined,
      produits: promotion.produits ?? [],
    };
  }
}
