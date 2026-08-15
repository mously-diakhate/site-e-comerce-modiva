import { beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpResponse } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { TranslateModule } from '@ngx-translate/core';
import { Subject, from, of } from 'rxjs';

import { IPanier } from 'app/entities/panier/panier.model';
import { PanierService } from 'app/entities/panier/service/panier.service';
import { UserService } from 'app/entities/user/service/user.service';
import { IUser } from 'app/entities/user/user.model';
import { IClienteProfile } from '../cliente-profile.model';
import { ClienteProfileService } from '../service/cliente-profile.service';

import { ClienteProfileFormService } from './cliente-profile-form.service';
import { ClienteProfileUpdate } from './cliente-profile-update';

describe('ClienteProfile Management Update Component', () => {
  let comp: ClienteProfileUpdate;
  let fixture: ComponentFixture<ClienteProfileUpdate>;
  let activatedRoute: ActivatedRoute;
  let clienteProfileFormService: ClienteProfileFormService;
  let clienteProfileService: ClienteProfileService;
  let userService: UserService;
  let panierService: PanierService;

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

    fixture = TestBed.createComponent(ClienteProfileUpdate);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clienteProfileFormService = TestBed.inject(ClienteProfileFormService);
    clienteProfileService = TestBed.inject(ClienteProfileService);
    userService = TestBed.inject(UserService);
    panierService = TestBed.inject(PanierService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('should call User query and add missing value', () => {
      const clienteProfile: IClienteProfile = { id: 25314 };
      const user: IUser = { id: 3944 };
      clienteProfile.user = user;

      const userCollection: IUser[] = [{ id: 3944 }];
      vitest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      vitest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clienteProfile });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(i => expect.objectContaining(i) as typeof i),
      );
      expect(comp.usersSharedCollection()).toEqual(expectedCollection);
    });

    it('should call panier query and add missing value', () => {
      const clienteProfile: IClienteProfile = { id: 25314 };
      const panier: IPanier = { id: 28828 };
      clienteProfile.panier = panier;

      const panierCollection: IPanier[] = [{ id: 28828 }];
      vitest.spyOn(panierService, 'query').mockReturnValue(of(new HttpResponse({ body: panierCollection })));
      const expectedCollection: IPanier[] = [panier, ...panierCollection];
      vitest.spyOn(panierService, 'addPanierToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ clienteProfile });
      comp.ngOnInit();

      expect(panierService.query).toHaveBeenCalled();
      expect(panierService.addPanierToCollectionIfMissing).toHaveBeenCalledWith(panierCollection, panier);
      expect(comp.paniersCollection()).toEqual(expectedCollection);
    });

    it('should update editForm', () => {
      const clienteProfile: IClienteProfile = { id: 25314 };
      const user: IUser = { id: 3944 };
      clienteProfile.user = user;
      const panier: IPanier = { id: 28828 };
      clienteProfile.panier = panier;

      activatedRoute.data = of({ clienteProfile });
      comp.ngOnInit();

      expect(comp.usersSharedCollection()).toContainEqual(user);
      expect(comp.paniersCollection()).toContainEqual(panier);
      expect(comp.clienteProfile).toEqual(clienteProfile);
    });
  });

  describe('save', () => {
    it('should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<IClienteProfile>();
      const clienteProfile = { id: 4325 };
      vitest.spyOn(clienteProfileFormService, 'getClienteProfile').mockReturnValue(clienteProfile);
      vitest.spyOn(clienteProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clienteProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(clienteProfile);
      saveSubject.complete();

      // THEN
      expect(clienteProfileFormService.getClienteProfile).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clienteProfileService.update).toHaveBeenCalledWith(expect.objectContaining(clienteProfile));
      expect(comp.isSaving()).toEqual(false);
    });

    it('should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<IClienteProfile>();
      const clienteProfile = { id: 4325 };
      vitest.spyOn(clienteProfileFormService, 'getClienteProfile').mockReturnValue({ id: null });
      vitest.spyOn(clienteProfileService, 'create').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clienteProfile: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.next(clienteProfile);
      saveSubject.complete();

      // THEN
      expect(clienteProfileFormService.getClienteProfile).toHaveBeenCalled();
      expect(clienteProfileService.create).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<IClienteProfile>();
      const clienteProfile = { id: 4325 };
      vitest.spyOn(clienteProfileService, 'update').mockReturnValue(saveSubject);
      vitest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ clienteProfile });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving()).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clienteProfileService.update).toHaveBeenCalled();
      expect(comp.isSaving()).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('should forward to userService', () => {
        const entity = { id: 3944 };
        const entity2 = { id: 6275 };
        vitest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePanier', () => {
      it('should forward to panierService', () => {
        const entity = { id: 28828 };
        const entity2 = { id: 8891 };
        vitest.spyOn(panierService, 'comparePanier');
        comp.comparePanier(entity, entity2);
        expect(panierService.comparePanier).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
