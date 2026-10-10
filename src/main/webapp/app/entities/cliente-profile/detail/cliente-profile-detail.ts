import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IClienteProfile } from '../cliente-profile.model';
import { ClienteProfileService } from '../service/cliente-profile.service';

@Component({
  selector: 'jhi-cliente-profile-detail',
  templateUrl: './cliente-profile-detail.html',
  styleUrl: './cliente-profile-detail.scss',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ClienteProfileDetail {
  readonly clienteProfile = input<IClienteProfile | null>(null);

  protected clienteProfileService = inject(ClienteProfileService);

  /** Bascule rapide actif/inactif depuis la page détail, sans formulaire d'édition. */
  toggleActif(): void {
    const current = this.clienteProfile();
    if (!current) {
      return;
    }
    const updated = { ...current, actif: !current.actif };
    this.clienteProfileService.update(updated).subscribe();
  }

  previousState(): void {
    globalThis.history.back();
  }
}
