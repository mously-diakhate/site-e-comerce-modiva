import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IClienteProfile } from '../cliente-profile.model';

@Component({
  selector: 'jhi-cliente-profile-detail',
  templateUrl: './cliente-profile-detail.html',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ClienteProfileDetail {
  readonly clienteProfile = input<IClienteProfile | null>(null);

  previousState(): void {
    globalThis.history.back();
  }
}
