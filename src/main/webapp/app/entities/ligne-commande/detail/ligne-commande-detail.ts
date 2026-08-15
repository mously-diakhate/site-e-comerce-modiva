import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { ILigneCommande } from '../ligne-commande.model';

@Component({
  selector: 'jhi-ligne-commande-detail',
  templateUrl: './ligne-commande-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class LigneCommandeDetail {
  readonly ligneCommande = input<ILigneCommande | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
