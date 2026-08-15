import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IVarianteProduit } from '../variante-produit.model';

@Component({
  selector: 'jhi-variante-produit-detail',
  templateUrl: './variante-produit-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class VarianteProduitDetail {
  readonly varianteProduit = input<IVarianteProduit | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
