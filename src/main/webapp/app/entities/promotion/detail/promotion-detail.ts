import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { FormatMediumDatetimePipe } from 'app/shared/date';
import { TranslateDirective } from 'app/shared/language';
import { IPromotion } from '../promotion.model';

@Component({
  selector: 'jhi-promotion-detail',
  templateUrl: './promotion-detail.html',
  styleUrl: './promotion-detail.scss',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink, FormatMediumDatetimePipe],
})
export class PromotionDetail {
  readonly promotion = input<IPromotion | null>(null);

  isExpired(promotion: IPromotion): boolean {
    return promotion.dateFin ? promotion.dateFin.isBefore() : false;
  }

  isActive(promotion: IPromotion): boolean {
    if (!promotion.dateDebut || !promotion.dateFin) {
      return false;
    }
    return promotion.dateDebut.isBefore() && promotion.dateFin.isAfter();
  }

  previousState(): void {
    globalThis.history.back();
  }
}
