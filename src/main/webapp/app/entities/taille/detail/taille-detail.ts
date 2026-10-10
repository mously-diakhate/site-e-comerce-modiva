import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ITaille } from '../taille.model';

@Component({
  selector: 'jhi-taille-detail',
  templateUrl: './taille-detail.html',
  styleUrl: './taille-detail.scss',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class TailleDetail {
  readonly taille = input<ITaille | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
