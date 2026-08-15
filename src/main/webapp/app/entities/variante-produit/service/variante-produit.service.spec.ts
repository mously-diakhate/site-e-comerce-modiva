import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IVarianteProduit } from '../variante-produit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../variante-produit.test-samples';

import { VarianteProduitService } from './variante-produit.service';

const requireRestSample: IVarianteProduit = {
  ...sampleWithRequiredData,
};

describe('VarianteProduit Service', () => {
  let service: VarianteProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: IVarianteProduit | IVarianteProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(VarianteProduitService);
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

    it('should create a VarianteProduit', () => {
      const varianteProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(varianteProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a VarianteProduit', () => {
      const varianteProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(varianteProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a VarianteProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of VarianteProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a VarianteProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addVarianteProduitToCollectionIfMissing', () => {
      it('should add a VarianteProduit to an empty array', () => {
        const varianteProduit: IVarianteProduit = sampleWithRequiredData;
        expectedResult = service.addVarianteProduitToCollectionIfMissing([], varianteProduit);
        expect(expectedResult).toEqual([varianteProduit]);
      });

      it('should not add a VarianteProduit to an array that contains it', () => {
        const varianteProduit: IVarianteProduit = sampleWithRequiredData;
        const varianteProduitCollection: IVarianteProduit[] = [
          {
            ...varianteProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addVarianteProduitToCollectionIfMissing(varianteProduitCollection, varianteProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a VarianteProduit to an array that doesn't contain it", () => {
        const varianteProduit: IVarianteProduit = sampleWithRequiredData;
        const varianteProduitCollection: IVarianteProduit[] = [sampleWithPartialData];
        expectedResult = service.addVarianteProduitToCollectionIfMissing(varianteProduitCollection, varianteProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(varianteProduit);
      });

      it('should add only unique VarianteProduit to an array', () => {
        const varianteProduitArray: IVarianteProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const varianteProduitCollection: IVarianteProduit[] = [sampleWithRequiredData];
        expectedResult = service.addVarianteProduitToCollectionIfMissing(varianteProduitCollection, ...varianteProduitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const varianteProduit: IVarianteProduit = sampleWithRequiredData;
        const varianteProduit2: IVarianteProduit = sampleWithPartialData;
        expectedResult = service.addVarianteProduitToCollectionIfMissing([], varianteProduit, varianteProduit2);
        expect(expectedResult).toEqual([varianteProduit, varianteProduit2]);
      });

      it('should accept null and undefined values', () => {
        const varianteProduit: IVarianteProduit = sampleWithRequiredData;
        expectedResult = service.addVarianteProduitToCollectionIfMissing([], null, varianteProduit, undefined);
        expect(expectedResult).toEqual([varianteProduit]);
      });

      it('should return initial array if no VarianteProduit is added', () => {
        const varianteProduitCollection: IVarianteProduit[] = [sampleWithRequiredData];
        expectedResult = service.addVarianteProduitToCollectionIfMissing(varianteProduitCollection, undefined, null);
        expect(expectedResult).toEqual(varianteProduitCollection);
      });
    });

    describe('compareVarianteProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareVarianteProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 24453 };
        const entity2 = null;

        const compareResult1 = service.compareVarianteProduit(entity1, entity2);
        const compareResult2 = service.compareVarianteProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 24453 };
        const entity2 = { id: 18856 };

        const compareResult1 = service.compareVarianteProduit(entity1, entity2);
        const compareResult2 = service.compareVarianteProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 24453 };
        const entity2 = { id: 24453 };

        const compareResult1 = service.compareVarianteProduit(entity1, entity2);
        const compareResult2 = service.compareVarianteProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
