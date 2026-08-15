import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILivraison } from '../livraison.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../livraison.test-samples';

import { LivraisonService } from './livraison.service';

const requireRestSample: ILivraison = {
  ...sampleWithRequiredData,
};

describe('Livraison Service', () => {
  let service: LivraisonService;
  let httpMock: HttpTestingController;
  let expectedResult: ILivraison | ILivraison[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LivraisonService);
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

    it('should create a Livraison', () => {
      const livraison = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(livraison).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Livraison', () => {
      const livraison = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(livraison).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Livraison', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Livraison', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Livraison', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLivraisonToCollectionIfMissing', () => {
      it('should add a Livraison to an empty array', () => {
        const livraison: ILivraison = sampleWithRequiredData;
        expectedResult = service.addLivraisonToCollectionIfMissing([], livraison);
        expect(expectedResult).toEqual([livraison]);
      });

      it('should not add a Livraison to an array that contains it', () => {
        const livraison: ILivraison = sampleWithRequiredData;
        const livraisonCollection: ILivraison[] = [
          {
            ...livraison,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLivraisonToCollectionIfMissing(livraisonCollection, livraison);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Livraison to an array that doesn't contain it", () => {
        const livraison: ILivraison = sampleWithRequiredData;
        const livraisonCollection: ILivraison[] = [sampleWithPartialData];
        expectedResult = service.addLivraisonToCollectionIfMissing(livraisonCollection, livraison);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(livraison);
      });

      it('should add only unique Livraison to an array', () => {
        const livraisonArray: ILivraison[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const livraisonCollection: ILivraison[] = [sampleWithRequiredData];
        expectedResult = service.addLivraisonToCollectionIfMissing(livraisonCollection, ...livraisonArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const livraison: ILivraison = sampleWithRequiredData;
        const livraison2: ILivraison = sampleWithPartialData;
        expectedResult = service.addLivraisonToCollectionIfMissing([], livraison, livraison2);
        expect(expectedResult).toEqual([livraison, livraison2]);
      });

      it('should accept null and undefined values', () => {
        const livraison: ILivraison = sampleWithRequiredData;
        expectedResult = service.addLivraisonToCollectionIfMissing([], null, livraison, undefined);
        expect(expectedResult).toEqual([livraison]);
      });

      it('should return initial array if no Livraison is added', () => {
        const livraisonCollection: ILivraison[] = [sampleWithRequiredData];
        expectedResult = service.addLivraisonToCollectionIfMissing(livraisonCollection, undefined, null);
        expect(expectedResult).toEqual(livraisonCollection);
      });
    });

    describe('compareLivraison', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLivraison(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 11785 };
        const entity2 = null;

        const compareResult1 = service.compareLivraison(entity1, entity2);
        const compareResult2 = service.compareLivraison(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 11785 };
        const entity2 = { id: 8459 };

        const compareResult1 = service.compareLivraison(entity1, entity2);
        const compareResult2 = service.compareLivraison(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 11785 };
        const entity2 = { id: 11785 };

        const compareResult1 = service.compareLivraison(entity1, entity2);
        const compareResult2 = service.compareLivraison(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
