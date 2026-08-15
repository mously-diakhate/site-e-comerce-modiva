import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IAdresse } from '../adresse.model';

@Component({
  selector: 'jhi-adresse-detail',
  templateUrl: './adresse-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class AdresseDetail {
  readonly adresse = input<IAdresse | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
