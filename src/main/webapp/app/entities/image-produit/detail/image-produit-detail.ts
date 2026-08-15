import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IImageProduit } from '../image-produit.model';

@Component({
  selector: 'jhi-image-produit-detail',
  templateUrl: './image-produit-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ImageProduitDetail {
  readonly imageProduit = input<IImageProduit | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
