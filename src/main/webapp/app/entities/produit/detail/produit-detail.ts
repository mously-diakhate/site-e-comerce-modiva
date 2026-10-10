import { HttpClient } from '@angular/common/http';
import { Component, OnInit, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';

import { IImageProduit } from 'app/entities/image-produit/image-produit.model';
import { IVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { DataUtils } from 'app/core/util/data-util.service';
import { Alert } from 'app/shared/alert/alert';
import { AlertError } from 'app/shared/alert/alert-error';
import { TranslateDirective } from 'app/shared/language';
import { IProduit } from '../produit.model';

@Component({
  selector: 'jhi-produit-detail',
  templateUrl: './produit-detail.html',
  styleUrl: './produit-detail.scss',
  imports: [FontAwesomeModule, Alert, AlertError, TranslateDirective, TranslateModule, RouterLink],
})
export class ProduitDetail implements OnInit {
  readonly produit = input<IProduit | null>(null);

  readonly images = signal<IImageProduit[]>([]);
  readonly activeImageIndex = signal(0);
  readonly variantes = signal<IVarianteProduit[]>([]);

  protected dataUtils = inject(DataUtils);
  protected http = inject(HttpClient);
  protected varianteProduitService = inject(VarianteProduitService);

  ngOnInit(): void {
    const produitId = this.produit()?.id;
    if (produitId) {
      // Images : endpoint dédié /by-produit/{id} car ImageProduitResource n'a pas de QueryService/Criteria.
      this.http.get<IImageProduit[]>(`api/image-produits/by-produit/${produitId}`).subscribe(images => {
        this.images.set(images ?? []);
      });

      // Variantes : VarianteProduitResource possède un vrai QueryService/Criteria, ce filtre fonctionne correctement.
      this.varianteProduitService.query({ 'produitId.equals': produitId }).subscribe(res => {
        this.variantes.set(res.body ?? []);
      });
    }
  }

  selectImage(index: number): void {
    this.activeImageIndex.set(index);
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    globalThis.history.back();
  }
}
