import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IClienteProfile } from 'app/entities/cliente-profile/cliente-profile.model';
import { ClienteProfileService } from 'app/entities/cliente-profile/service/cliente-profile.service';
import { IAdresse } from '../adresse.model';
import { AdresseService } from '../service/adresse.service';

import { AdresseFormService } from './adresse-form.service';
import { AdresseUpdate } from './adresse-update';

describe('Adresse Management Update Component', () => {
  let comp: AdresseUpdate;
  let fixture: ComponentFixture<AdresseUpdate>;
  let activatedRoute: ActivatedRoute;
  let adresseFormService: AdresseFormService;
  let adresseService: AdresseService;
  let clienteProfileService: ClienteProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    });

    fixture = TestBed.createComponent(AdresseUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    adresseFormService = TestBed.inject(AdresseFormService);
    adresseService = TestBed.inject(AdresseService);
    clienteProfileService = TestBed.inject(ClienteProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call ClienteProfile query and add missing value', () => {
      const adresse: IAdresse = { id: 16957 };
      const clienteProfile: IClienteProfile = { id: 4325 };
      adresse.clienteProfile = clienteProfile;

      const clienteProfileCollection: IClienteProfile[] = [{ id: 4325 }];
      vitest.spyOn(clienteProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: clienteProfileCollection })));
      const additionalClienteProfiles = [clienteProfile];
      const expectedCollection: IClienteProfile[] = [...additionalClienteProfiles, ...clienteProfileCollection];
      vitest.spyOn(clienteProfileService, 'addClienteProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ adresse });
      comp.ngOnInit();

      expect(clienteProfileService.query).toHaveBeenCalled();
      expect(clienteProfileService.addClienteProfileToCollectionIfMissing).toHaveBeenCalledWith(
        clienteProfileCollection,
        ...additionalClienteProfiles.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.clienteProfilesSharedCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const adresse: IAdresse = { id: 16957 };
      const clienteProfile: IClienteProfile = { id: 4325 };
      adresse.clienteProfile = clienteProfile;

      activatedRoute.data = of({ adresse });
      comp.ngOnInit();

      expect(comp.clienteProfilesSharedCollection()).toContainEqual(clienteProfile);
      expect(comp.adresse).toEqual(adresse);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAdresse>();
      const adresse = { id: 31795 };
      vitest.spyOn(adresseFormService, 'getAdresse').mockReturnValue(adresse);
      vitest.spyOn(adresseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ adresse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(adresse);
      saveSubject.complete();

      // THEN
      expect(adresseFormService.getAdresse).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(adresseService.update).toHaveBeenCalledWith(expect.objectContaining(adresse));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IAdresse>();
      const adresse = { id: 31795 };
      vitest.spyOn(adresseFormService, 'getAdresse').mockReturnValue({ id: null });
      vitest.spyOn(adresseService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ adresse: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(adresse);
      saveSubject.complete();

      // THEN
      expect(adresseFormService.getAdresse).toHaveBeenCalled();
      expect(adresseService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IAdresse>();
      const adresse = { id: 31795 };
      vitest.spyOn(adresseService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ adresse });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(adresseService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareClienteProfile', () => {
      it('should forward to clienteProfileService', () => {
        const entity = { id: 4325 };
        const entity2 = { id: 25314 };
        vitest.spyOn(clienteProfileService, 'compareClienteProfile');
        comp.compareClienteProfile(entity, entity2);
        expect(clienteProfileService.compareClienteProfile).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
