import { MockInstance, afterEach, beforeEach, describe, expect, it, vitest } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, inject } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';

import { FaIconLibrary } from '@fortawesome/angular-fontawesome';
import { faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes } from '@fortawesome/free-solid-svg-icons';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap/modal';
import { TranslateModule } from '@ngx-translate/core';
import { Subject, of } from 'rxjs';

import { sampleWithRequiredData } from '../image-produit.test-samples';
import { ImageProduitService } from '../service/image-produit.service';

import { ImageProduit } from './image-produit';

vitest.useFakeTimers();

describe('ImageProduit Management Component', () => {
  let httpMock: HttpTestingController;
  let comp: ImageProduit;
  let fixture: ComponentFixture<ImageProduit>;
  let service: ImageProduitService;
  let routerNavigateSpy: MockInstance;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [
        provideHttpClientTesting(),
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            ),
            snapshot: {
              queryParams: {},
              queryParamMap: convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              }),
            },
          },
        },
      ],
    });

    fixture = TestBed.createComponent(ImageProduit);
    comp = fixture.componentInstance;
    service = TestBed.inject(ImageProduitService);
    routerNavigateSpy = vitest.spyOn(comp.router, 'navigate');

    const library = TestBed.inject(FaIconLibrary);
    library.addIcons(faEye, faPencilAlt, faPlus, faSort, faSortDown, faSortUp, faSync, faTimes);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    TestBed.resetTestingModule();
    httpMock.verify();
  });

  it('should call load all on init', async () => {
    // WHEN
    TestBed.tick();
    const req = httpMock.expectOne({ method: 'GET' });
    req.flush([{ id: 15619 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();

    // THEN
    expect(comp.isLoading()).toEqual(false);
    expect(comp.imageProduits()[0]).toEqual(expect.objectContaining({ id: 15619 }));
  });

  describe('trackId', () => {
    it('should forward to imageProduitService', () => {
      const entity = { id: 15619 };
      vitest.spyOn(service, 'getImageProduitIdentifier');
      const id = comp.trackId(entity);
      expect(service.getImageProduitIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });

  it('should calculate the sort attribute for a non-id attribute', () => {
    // WHEN
    comp.navigateToWithComponentValues({ predicate: 'non-existing-column', order: 'asc' });

    // THEN
    expect(routerNavigateSpy).toHaveBeenLastCalledWith(
      expect.anything(),
      expect.objectContaining({
        queryParams: expect.objectContaining({
          sort: ['non-existing-column,asc'],
        }),
      }),
    );
  });

  it('should calculate the sort attribute for an id', () => {
    // WHEN
    TestBed.tick();
    httpMock.expectOne({ method: 'GET' });

    // THEN
    expect(service.imageProduitsParams()).toMatchObject(expect.objectContaining({ sort: ['id,desc'] }));
  });

  it('should infinite scroll', async () => {
    // GIVEN
    TestBed.tick();
    let req = httpMock.expectOne({ method: 'GET' });
    req.flush([{ id: 15619 }], { headers: { link: '<http://localhost/api/foo?page=1&size=20>; rel="next"' } });
    await vitest.runAllTimersAsync();
    expect(comp.imageProduits().length).toEqual(1);
    expect(comp.imageProduits()[0]).toEqual(expect.objectContaining({ id: 15619 }));

    // WHEN
    comp.loadNextPage();
    TestBed.tick();
    expect(service.imageProduitsParams()).toMatchObject(expect.objectContaining({ page: '1' }));
    req = httpMock.expectOne({ method: 'GET' });
    req.flush([{ id: 204 }], {
      headers: { link: '<http://localhost/api/foo?page=0&size=20>; rel="prev",<http://localhost/api/foo?page=2&size=20>; rel="next"' },
    });
    await vitest.runAllTimersAsync();
    expect(comp.imageProduits().length).toEqual(2);
    expect(comp.imageProduits()[1]).toEqual(expect.objectContaining({ id: 204 }));

    comp.loadNextPage();
    TestBed.tick();
    expect(service.imageProduitsParams()).toMatchObject(expect.objectContaining({ page: '2' }));
    req = httpMock.expectOne({ method: 'GET' });
    req.flush([{ id: 204 }], {
      headers: { link: '<http://localhost/api/foo?page=0&size=20>; rel="prev",<http://localhost/api/foo?page=2&size=20>; rel="next"' },
    });
    await vitest.runAllTimersAsync();
    expect(comp.imageProduits().length).toEqual(2);
    expect(comp.imageProduits()[1]).toEqual(expect.objectContaining({ id: 204 }));
  });

  describe('delete', () => {
    let ngbModal: NgbModal;
    let deleteModalMock: any;

    beforeEach(() => {
      deleteModalMock = { componentInstance: {}, closed: new Subject() };
      // NgbModal is not a singleton using TestBed.inject.
      // ngbModal = TestBed.inject(NgbModal);
      ngbModal = (comp as any).modalService;
      vitest.spyOn(ngbModal, 'open').mockReturnValue(deleteModalMock);
    });

    it('on confirm should call load', inject([], () => {
      // GIVEN
      vitest.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next('deleted');

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).toHaveBeenCalled();
    }));

    it('on dismiss should call load', inject([], () => {
      // GIVEN
      vitest.spyOn(comp, 'load');

      // WHEN
      comp.delete(sampleWithRequiredData);
      deleteModalMock.closed.next();

      // THEN
      expect(ngbModal.open).toHaveBeenCalled();
      expect(comp.load).not.toHaveBeenCalled();
    }));
  });
});
