import { Component, input } from '@angular/core';
import { NgClass } from '@angular/common';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { ICommande } from '../commande.model';

const STATUT_LABELS: Record<string, string> = {
  EN_ATTENTE: 'En attente',
  CONFIRMEE: 'Confirmée',
  EN_PREPARATION: 'En préparation',
  EXPEDIEE: 'Expédiée',
  LIVREE: 'Livrée',
  ANNULEE: 'Annulée',
};
const MODE_PAIEMENT_LABELS: Record<string, string> = {
  WAVE: 'Wave',
  ORANGE_MONEY: 'Orange Money',
  SUR_PLACE: 'Sur place',
};

@Component({
  selector: 'jhi-commande-detail',
  templateUrl: './commande-detail.html',
  styleUrl: './commande-detail.scss',
  imports: [NgClass, FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatetimePipe],
})
export class CommandeDetail {
  readonly commande = input<ICommande | null>(null);

  statutLabel(statut: string | null | undefined): string {
    return statut ? (STATUT_LABELS[statut] ?? statut) : '—';
  }

  statutClass(statut: string | null | undefined): string {
    return statut ? `statut-${statut.toLowerCase().replace(/_/g, '-')}` : '';
  }

  previousState(): void {
    globalThis.history.back();
  }
  modePaiementLabel(mode: string | null | undefined): string {
    return mode ? (MODE_PAIEMENT_LABELS[mode] ?? mode) : '—';
  }
}
