import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICouleur } from '../couleur.model';

@Component({
  selector: 'jhi-couleur-detail',
  templateUrl: './couleur-detail.html',
  styleUrl: './couleur-detail.scss',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class CouleurDetail {
  readonly couleur = input<ICouleur | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
