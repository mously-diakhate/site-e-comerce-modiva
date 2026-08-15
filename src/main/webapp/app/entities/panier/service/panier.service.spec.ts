import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IPanier } from '../panier.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../panier.test-samples';

import { PanierService, RestPanier } from './panier.service';

const requireRestSample: RestPanier = {
  ...sampleWithRequiredData,
  dateCreation: sampleWithRequiredData.dateCreation?.toJSON(),
};

describe('Panier Service', () => {
  let service: PanierService;
  let httpMock: HttpTestingController;
  let expectedResult: IPanier | IPanier[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(PanierService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Panier', () => {
      const panier = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(panier).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Panier', () => {
      const panier = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(panier).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Panier', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Panier', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Panier', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addPanierToCollectionIfMissing', () => {
      it('should add a Panier to an empty array', () => {
        const panier: IPanier = sampleWithRequiredData;
        expectedResult = service.addPanierToCollectionIfMissing([], panier);
        expect(expectedResult).toEqual([panier]);
      });

      it('should not add a Panier to an array that contains it', () => {
        const panier: IPanier = sampleWithRequiredData;
        const panierCollection: IPanier[] = [
          {
            ...panier,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addPanierToCollectionIfMissing(panierCollection, panier);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Panier to an array that doesn't contain it", () => {
        const panier: IPanier = sampleWithRequiredData;
        const panierCollection: IPanier[] = [sampleWithPartialData];
        expectedResult = service.addPanierToCollectionIfMissing(panierCollection, panier);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(panier);
      });

      it('should add only unique Panier to an array', () => {
        const panierArray: IPanier[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const panierCollection: IPanier[] = [sampleWithRequiredData];
        expectedResult = service.addPanierToCollectionIfMissing(panierCollection, ...panierArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const panier: IPanier = sampleWithRequiredData;
        const panier2: IPanier = sampleWithPartialData;
        expectedResult = service.addPanierToCollectionIfMissing([], panier, panier2);
        expect(expectedResult).toEqual([panier, panier2]);
      });

      it('should accept null and undefined values', () => {
        const panier: IPanier = sampleWithRequiredData;
        expectedResult = service.addPanierToCollectionIfMissing([], null, panier, undefined);
        expect(expectedResult).toEqual([panier]);
      });

      it('should return initial array if no Panier is added', () => {
        const panierCollection: IPanier[] = [sampleWithRequiredData];
        expectedResult = service.addPanierToCollectionIfMissing(panierCollection, undefined, null);
        expect(expectedResult).toEqual(panierCollection);
      });
    });

    describe('comparePanier', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.comparePanier(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 28828 };
        const entity2 = null;

        const compareResult1 = service.comparePanier(entity1, entity2);
        const compareResult2 = service.comparePanier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 28828 };
        const entity2 = { id: 8891 };

        const compareResult1 = service.comparePanier(entity1, entity2);
        const compareResult2 = service.comparePanier(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 28828 };
        const entity2 = { id: 28828 };

        const compareResult1 = service.comparePanier(entity1, entity2);
        const compareResult2 = service.comparePanier(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
