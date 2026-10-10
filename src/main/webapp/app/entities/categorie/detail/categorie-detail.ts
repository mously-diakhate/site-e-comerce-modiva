import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ICategorie } from '../categorie.model';

@Component({
  selector: 'jhi-categorie-detail',
  templateUrl: './categorie-detail.html',
  styleUrl: './categorie-detail.scss',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class CategorieDetail {
  readonly categorie = input<ICategorie | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
