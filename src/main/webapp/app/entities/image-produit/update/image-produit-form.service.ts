import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IImageProduit, NewImageProduit } from '../image-produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IImageProduit for edit and NewImageProduitFormGroupInput for create.
 */
type ImageProduitFormGroupInput = IImageProduit | PartialWithRequiredKeyOf<NewImageProduit>;

type ImageProduitFormDefaults = Pick<NewImageProduit, 'id'>;

type ImageProduitFormGroupContent = {
  id: FormControl<IImageProduit['id'] | NewImageProduit['id']>;
  url: FormControl<IImageProduit['url']>;
  ordre: FormControl<IImageProduit['ordre']>;
  produit: FormControl<IImageProduit['produit']>;
};

export type ImageProduitFormGroup = FormGroup<ImageProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ImageProduitFormService {
  createImageProduitFormGroup(imageProduit?: ImageProduitFormGroupInput): ImageProduitFormGroup {
    const imageProduitRawValue = {
      ...this.getFormDefaults(),
      ...(imageProduit ?? { id: null }),
    };
    return new FormGroup<ImageProduitFormGroupContent>({
      id: new FormControl(
        { value: imageProduitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      url: new FormControl(imageProduitRawValue.url, {
        validators: [Validators.required],
      }),
      ordre: new FormControl(imageProduitRawValue.ordre),
      produit: new FormControl(imageProduitRawValue.produit, {
        validators: [Validators.required],
      }),
    });
  }

  getImageProduit(form: ImageProduitFormGroup): IImageProduit | NewImageProduit {
    return form.getRawValue() as IImageProduit | NewImageProduit;
  }

  resetForm(form: ImageProduitFormGroup, imageProduit: ImageProduitFormGroupInput): void {
    const imageProduitRawValue = { ...this.getFormDefaults(), ...imageProduit };
    form.reset({
      ...imageProduitRawValue,
      id: { value: imageProduitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ImageProduitFormDefaults {
    return {
      id: null,
    };
  }
}
