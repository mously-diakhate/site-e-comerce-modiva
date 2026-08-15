import { Injectable } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { IProduit, NewProduit } from '../produit.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IProduit for edit and NewProduitFormGroupInput for create.
 */
type ProduitFormGroupInput = IProduit | PartialWithRequiredKeyOf<NewProduit>;

type ProduitFormDefaults = Pick<NewProduit, 'id' | 'promotions'>;

type ProduitFormGroupContent = {
  id: FormControl<IProduit['id'] | NewProduit['id']>;
  nom: FormControl<IProduit['nom']>;
  description: FormControl<IProduit['description']>;
  prix: FormControl<IProduit['prix']>;
  statut: FormControl<IProduit['statut']>;
  promotions: FormControl<IProduit['promotions']>;
  categorie: FormControl<IProduit['categorie']>;
};

export type ProduitFormGroup = FormGroup<ProduitFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ProduitFormService {
  createProduitFormGroup(produit?: ProduitFormGroupInput): ProduitFormGroup {
    const produitRawValue = {
      ...this.getFormDefaults(),
      ...(produit ?? { id: null }),
    };
    return new FormGroup<ProduitFormGroupContent>({
      id: new FormControl(
        { value: produitRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      nom: new FormControl(produitRawValue.nom, {
        validators: [Validators.required],
      }),
      description: new FormControl(produitRawValue.description),
      prix: new FormControl(produitRawValue.prix, {
        validators: [Validators.required, Validators.min(0)],
      }),
      statut: new FormControl(produitRawValue.statut, {
        validators: [Validators.required],
      }),
      promotions: new FormControl(produitRawValue.promotions ?? []),
      categorie: new FormControl(produitRawValue.categorie, {
        validators: [Validators.required],
      }),
    });
  }

  getProduit(form: ProduitFormGroup): IProduit | NewProduit {
    return form.getRawValue() as IProduit | NewProduit;
  }

  resetForm(form: ProduitFormGroup, produit: ProduitFormGroupInput): void {
    const produitRawValue = { ...this.getFormDefaults(), ...produit };
    form.reset({
      ...produitRawValue,
      id: { value: produitRawValue.id, disabled: true },
    });
  }

  private getFormDefaults(): ProduitFormDefaults {
    return {
      id: null,
      promotions: [],
    };
  }
}
