import { HttpClient, HttpResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { TranslateModule } from '@ngx-translate/core';
import { Observable, forkJoin, of } from 'rxjs';
import { catchError, finalize, map } from 'rxjs/operators';

import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { ICategorie, NewCategorie } from 'app/entities/categorie/categorie.model';
import { CategorieService } from 'app/entities/categorie/service/categorie.service';
// Adaptez ces chemins si vos dossiers générés diffèrent.
import { ICouleur, NewCouleur } from 'app/entities/couleur/couleur.model';
import { CouleurService } from 'app/entities/couleur/service/couleur.service';
import { StatutProduit } from 'app/entities/enumerations/statut-produit.model';
import { IImageProduit, NewImageProduit } from 'app/entities/image-produit/image-produit.model';
import { ImageProduitService } from 'app/entities/image-produit/service/image-produit.service';
import { IPromotion } from 'app/entities/promotion/promotion.model';
import { PromotionService } from 'app/entities/promotion/service/promotion.service';
import { ITaille, NewTaille } from 'app/entities/taille/taille.model';
import { TailleService } from 'app/entities/taille/service/taille.service';
import { IVarianteProduit, NewVarianteProduit } from 'app/entities/variante-produit/variante-produit.model';
import { VarianteProduitService } from 'app/entities/variante-produit/service/variante-produit.service';
import { AlertError } from 'app/shared/alert/alert-error';
import { AlertErrorModel } from 'app/shared/alert/alert-error.model';
import { TranslateDirective } from 'app/shared/language';

import { IProduit } from '../produit.model';
import { ProduitService } from '../service/produit.service';
import { ProduitFormGroup, ProduitFormService } from './produit-form.service';

/** Image en attente d'envoi, pas encore liée à un produit persistant. */
interface PendingImage {
  localId: string;
  preview: string;
}

/** Variante (taille + couleur + stock) en attente d'envoi, pas encore liée à un produit persistant. */
interface PendingVariante {
  localId: string;
  tailleId: number | null;
  couleurId: number | null;
  stock: number | null;
}

@Component({
  selector: 'jhi-produit-update',
  templateUrl: './produit-update.html',
  styleUrl: './produit-update.scss',
  imports: [TranslateDirective, TranslateModule, FontAwesomeModule, AlertError, ReactiveFormsModule],
})
export class ProduitUpdate implements OnInit {
  readonly isSaving = signal(false);
  readonly isUploadingImages = signal(false);
  produit: IProduit | null = null;
  statutProduitValues = Object.keys(StatutProduit);

  promotionsSharedCollection = signal<IPromotion[]>([]);
  categoriesSharedCollection = signal<ICategorie[]>([]);

  /** Images déjà enregistrées côté serveur (mode édition uniquement). */
  readonly existingImages = signal<IImageProduit[]>([]);
  /** Images choisies par l'admin et pas encore envoyées. */
  readonly pendingImages = signal<PendingImage[]>([]);

  /** Tailles et couleurs disponibles dans le catalogue (référentiels). */
  readonly taillesCollection = signal<ITaille[]>([]);
  readonly couleursCollection = signal<ICouleur[]>([]);
  /** Variantes déjà enregistrées côté serveur (mode édition uniquement). */
  readonly existingVariantes = signal<IVarianteProduit[]>([]);
  /** Lignes de variantes ajoutées par l'admin et pas encore envoyées. */
  readonly pendingVariantes = signal<PendingVariante[]>([]);

  /** Création inline d'une nouvelle taille depuis le formulaire produit. */
  readonly showNewTailleForm = signal(false);
  readonly newTailleLibelle = signal('');
  readonly isSavingTaille = signal(false);

  /** Création inline d'une nouvelle couleur depuis le formulaire produit. */
  readonly showNewCouleurForm = signal(false);
  readonly newCouleurLibelle = signal('');
  readonly newCouleurHex = signal('#000000');
  readonly isSavingCouleur = signal(false);

  /** Création inline d'une nouvelle catégorie depuis le formulaire produit. */
  readonly showNewCategorieForm = signal(false);
  readonly newCategorieNom = signal('');
  readonly newCategorieDescription = signal('');
  readonly isSavingCategorie = signal(false);

  protected dataUtils = inject(DataUtils);
  protected eventManager = inject(EventManager);
  protected produitService = inject(ProduitService);
  protected produitFormService = inject(ProduitFormService);
  protected promotionService = inject(PromotionService);
  protected categorieService = inject(CategorieService);
  protected imageProduitService = inject(ImageProduitService);
  protected tailleService = inject(TailleService);
  protected couleurService = inject(CouleurService);
  protected varianteProduitService = inject(VarianteProduitService);
  protected activatedRoute = inject(ActivatedRoute);
  protected http = inject(HttpClient); // NEW : pour appeler le endpoint dédié /by-produit/{id}

  // eslint-disable-next-line @typescript-eslint/member-ordering
  editForm: ProduitFormGroup = this.produitFormService.createProduitFormGroup();

  comparePromotion = (o1: IPromotion | null, o2: IPromotion | null): boolean => this.promotionService.comparePromotion(o1, o2);

  compareCategorie = (o1: ICategorie | null, o2: ICategorie | null): boolean => this.categorieService.compareCategorie(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produit }) => {
      this.produit = produit;
      if (produit) {
        this.updateForm(produit);
        this.loadExistingImages(produit.id);
        this.loadExistingVariantes(produit.id);
      }

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertErrorModel>('modivaApp.error', { ...err, key: `error.file.${err.key}` })),
    });
  }

  // ---------------------------------------------------------------------
  // Promotion — tags cliquables (remplace le <select multiple>)
  // ---------------------------------------------------------------------

  isPromotionSelected(promotion: IPromotion): boolean {
    const current: IPromotion[] = this.editForm.get('promotions')!.value ?? [];
    return current.some(selected => this.comparePromotion(selected, promotion));
  }

  togglePromotion(promotion: IPromotion): void {
    const control = this.editForm.get('promotions')!;
    const current: IPromotion[] = control.value ?? [];

    const next = this.isPromotionSelected(promotion)
      ? current.filter(selected => !this.comparePromotion(selected, promotion))
      : [...current, promotion];

    control.setValue(next);
    control.markAsDirty();
  }

  // ---------------------------------------------------------------------
  // Variantes — taille + couleur + stock
  // ---------------------------------------------------------------------

  /** true si au moins une ligne en attente n'a pas encore taille + couleur + stock renseignés. */
  hasIncompleteVariantes(): boolean {
    return this.pendingVariantes().some(v => v.tailleId === null || v.couleurId === null || v.stock === null || v.stock < 0);
  }

  addVarianteRow(): void {
    this.pendingVariantes.update(list => [
      ...list,
      { localId: `variante-${Date.now()}-${Math.random()}`, tailleId: null, couleurId: null, stock: null },
    ]);
  }

  removePendingVariante(localId: string): void {
    this.pendingVariantes.update(list => list.filter(v => v.localId !== localId));
  }

  updatePendingVarianteTaille(localId: string, tailleIdRaw: string): void {
    const tailleId = tailleIdRaw ? Number(tailleIdRaw) : null;
    this.pendingVariantes.update(list => list.map(v => (v.localId === localId ? { ...v, tailleId } : v)));
  }

  selectPendingVarianteCouleur(localId: string, couleurId: number): void {
    this.pendingVariantes.update(list => list.map(v => (v.localId === localId ? { ...v, couleurId } : v)));
  }

  updatePendingVarianteStock(localId: string, stockRaw: number): void {
    const stock = Number.isNaN(stockRaw) ? null : stockRaw;
    this.pendingVariantes.update(list => list.map(v => (v.localId === localId ? { ...v, stock } : v)));
  }

  removeExistingVariante(variante: IVarianteProduit): void {
    if (!globalThis.confirm('Supprimer définitivement cette variante ?')) {
      return;
    }
    this.varianteProduitService.delete(variante.id).subscribe(() => {
      this.existingVariantes.update(list => list.filter(v => v.id !== variante.id));
    });
  }

  updateExistingVarianteStock(variante: IVarianteProduit, stockRaw: number): void {
    if (Number.isNaN(stockRaw) || stockRaw < 0) {
      return;
    }
    this.varianteProduitService.update({ ...variante, stock: stockRaw }).subscribe(updated => {
      this.existingVariantes.update(list => list.map(v => (v.id === updated.id ? updated : v)));
    });
  }

  protected loadExistingVariantes(produitId: number): void {
    // Inchangé : VarianteProduitResource possède un vrai QueryService/Criteria, ce filtre fonctionne correctement.
    this.varianteProduitService.query({ 'produitId.equals': produitId }).subscribe(res => {
      this.existingVariantes.set(res.body ?? []);
    });
  }

  // ---------------------------------------------------------------------
  // Création inline de tailles / couleurs / catégories (sans quitter le formulaire)
  // ---------------------------------------------------------------------

  toggleNewTailleForm(): void {
    this.showNewTailleForm.update(v => !v);
    this.newTailleLibelle.set('');
  }

  saveNewTaille(): void {
    const libelle = this.newTailleLibelle().trim();
    if (!libelle) {
      return;
    }
    this.isSavingTaille.set(true);
    const payload: NewTaille = { id: null, libelle };
    this.tailleService.create(payload).subscribe({
      next: created => {
        this.taillesCollection.update(list => [...list, created]);
        this.showNewTailleForm.set(false);
        this.newTailleLibelle.set('');
        this.isSavingTaille.set(false);
      },
      error: () => this.isSavingTaille.set(false),
    });
  }

  toggleNewCouleurForm(): void {
    this.showNewCouleurForm.update(v => !v);
    this.newCouleurLibelle.set('');
    this.newCouleurHex.set('#000000');
  }

  saveNewCouleur(): void {
    const libelle = this.newCouleurLibelle().trim();
    if (!libelle) {
      return;
    }
    this.isSavingCouleur.set(true);
    const payload: NewCouleur = { id: null, libelle, codeHex: this.newCouleurHex() };
    this.couleurService.create(payload).subscribe({
      next: created => {
        this.couleursCollection.update(list => [...list, created]);
        this.showNewCouleurForm.set(false);
        this.newCouleurLibelle.set('');
        this.isSavingCouleur.set(false);
      },
      error: () => this.isSavingCouleur.set(false),
    });
  }

  /** Ouvre/ferme le mini-formulaire de création de catégorie sous le select. */
  toggleNewCategorieForm(): void {
    this.showNewCategorieForm.update(v => !v);
    this.newCategorieNom.set('');
    this.newCategorieDescription.set('');
  }

  /** Crée la catégorie via l'API, l'ajoute à la liste déroulante et la sélectionne directement dans le formulaire. */
  saveNewCategorie(): void {
    const nom = this.newCategorieNom().trim();
    if (!nom) {
      return;
    }
    this.isSavingCategorie.set(true);
    const payload: NewCategorie = { id: null, nom, description: this.newCategorieDescription().trim() || null };
    this.categorieService.create(payload).subscribe({
      next: created => {
        this.categoriesSharedCollection.update(list => [...list, created]);
        // Sélectionne automatiquement la catégorie qui vient d'être créée.
        this.editForm.get('categorie')!.setValue(created);
        this.editForm.get('categorie')!.markAsDirty();
        this.showNewCategorieForm.set(false);
        this.newCategorieNom.set('');
        this.newCategorieDescription.set('');
        this.isSavingCategorie.set(false);
      },
      error: () => this.isSavingCategorie.set(false),
    });
  }

  // ---------------------------------------------------------------------
  // Gestion des images du produit
  // ---------------------------------------------------------------------

  /** Nombre total d'images (déjà enregistrées + en attente) — utile pour l'affichage "principale". */
  totalImagesCount(): number {
    return this.existingImages().length + this.pendingImages().length;
  }

  onImagesSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    const files = input.files;
    if (!files) {
      return;
    }
    for (const file of Array.from(files)) {
      if (!file.type.startsWith('image/')) {
        continue;
      }
      const reader = new FileReader();
      reader.onload = () => {
        this.pendingImages.update(list => [
          ...list,
          { localId: `${file.name}-${Date.now()}-${Math.random()}`, preview: reader.result as string },
        ]);
      };
      reader.readAsDataURL(file);
    }
    // Permet de re-sélectionner le même fichier plus tard.
    input.value = '';
  }

  removePendingImage(localId: string): void {
    this.pendingImages.update(list => list.filter(img => img.localId !== localId));
  }

  removeExistingImage(image: IImageProduit): void {
    if (!globalThis.confirm('Supprimer définitivement cette image ?')) {
      return;
    }
    this.imageProduitService.delete(image.id).subscribe(() => {
      this.existingImages.update(list => list.filter(img => img.id !== image.id));
    });
  }

  protected loadExistingImages(produitId: number): void {
    // CORRIGÉ : ImageProduitResource n'a pas de QueryService/Criteria,
    // donc 'produitId.equals' était ignoré et renvoyait n'importe quelle image.
    // On utilise désormais le endpoint dédié /api/image-produits/by-produit/{id}.
    this.http.get<IImageProduit[]>(`api/image-produits/by-produit/${produitId}`).subscribe(images => {
      this.existingImages.set(images ?? []);
    });
  }

  // ---------------------------------------------------------------------
  // Enregistrement
  // ---------------------------------------------------------------------

  previousState(): void {
    globalThis.history.back();
  }

  save(): void {
    this.isSaving.set(true);
    const produit = this.produitFormService.getProduit(this.editForm);
    if (produit.id === null) {
      this.subscribeToSaveResponse(this.produitService.create(produit));
    } else {
      this.subscribeToSaveResponse(this.produitService.update(produit));
    }
  }

  protected subscribeToSaveResponse(result: Observable<IProduit | null>): void {
    result.pipe(finalize(() => this.isSaving.set(false))).subscribe({
      next: savedProduit => this.onSaveSuccess(savedProduit),
      error: () => this.onSaveError(),
    });
  }

  /** Une fois le produit enregistré, on envoie les images et les variantes en attente puis on quitte la page. */
  protected onSaveSuccess(savedProduit: IProduit | null): void {
    const pendingImgs = this.pendingImages();
    const pendingVars = this.pendingVariantes();

    if (!savedProduit || (pendingImgs.length === 0 && pendingVars.length === 0)) {
      this.previousState();
      return;
    }

    this.isUploadingImages.set(true);
    const startIndex = this.existingImages().length;

    const imageUploads = pendingImgs.map((img, i) => {
      const newImage: NewImageProduit = {
        id: null,
        url: img.preview,
        ordre: startIndex + i,
        produit: { id: savedProduit.id },
      };
      return this.imageProduitService.create(newImage).pipe(catchError(() => of(null)));
    });

    const varianteUploads = pendingVars
      .filter(v => v.tailleId !== null && v.couleurId !== null && v.stock !== null)
      .map(v => {
        const newVariante: NewVarianteProduit = {
          id: null,
          stock: v.stock!,
          taille: { id: v.tailleId! },
          couleur: { id: v.couleurId! },
          produit: { id: savedProduit.id },
        };
        return this.varianteProduitService.create(newVariante).pipe(catchError(() => of(null)));
      });

    forkJoin([...imageUploads, ...varianteUploads])
      .pipe(finalize(() => this.isUploadingImages.set(false)))
      .subscribe(() => this.previousState());
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected updateForm(produit: IProduit): void {
    this.produit = produit;
    this.produitFormService.resetForm(this.editForm, produit);

    this.promotionsSharedCollection.update(promotions =>
      this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(promotions, ...(produit.promotions ?? [])),
    );
    this.categoriesSharedCollection.update(categories =>
      this.categorieService.addCategorieToCollectionIfMissing<ICategorie>(categories, produit.categorie),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.promotionService
      .query()
      .pipe(map((res: HttpResponse<IPromotion[]>) => res.body ?? []))
      .pipe(
        map((promotions: IPromotion[]) =>
          this.promotionService.addPromotionToCollectionIfMissing<IPromotion>(promotions, ...(this.produit?.promotions ?? [])),
        ),
      )
      .subscribe((promotions: IPromotion[]) => this.promotionsSharedCollection.set(promotions));

    this.categorieService
      .query()
      .pipe(map((res: HttpResponse<ICategorie[]>) => res.body ?? []))
      .pipe(
        map((categories: ICategorie[]) =>
          this.categorieService.addCategorieToCollectionIfMissing<ICategorie>(categories, this.produit?.categorie),
        ),
      )
      .subscribe((categories: ICategorie[]) => this.categoriesSharedCollection.set(categories));

    this.tailleService
      .query()
      .pipe(map((res: HttpResponse<ITaille[]>) => res.body ?? []))
      .subscribe((tailles: ITaille[]) => this.taillesCollection.set(tailles));

    this.couleurService
      .query()
      .pipe(map((res: HttpResponse<ICouleur[]>) => res.body ?? []))
      .subscribe((couleurs: ICouleur[]) => this.couleursCollection.set(couleurs));
  }
}
