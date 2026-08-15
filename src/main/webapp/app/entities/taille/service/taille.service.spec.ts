import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ITaille } from '../taille.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../taille.test-samples';

import { TailleService } from './taille.service';

const requireRestSample: ITaille = {
  ...sampleWithRequiredData,
};

describe('Taille Service', () => {
  let service: TailleService;
  let httpMock: HttpTestingController;
  let expectedResult: ITaille | ITaille[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(TailleService);
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

    it('should create a Taille', () => {
      const taille = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(taille).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Taille', () => {
      const taille = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(taille).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Taille', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Taille', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Taille', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addTailleToCollectionIfMissing', () => {
      it('should add a Taille to an empty array', () => {
        const taille: ITaille = sampleWithRequiredData;
        expectedResult = service.addTailleToCollectionIfMissing([], taille);
        expect(expectedResult).toEqual([taille]);
      });

      it('should not add a Taille to an array that contains it', () => {
        const taille: ITaille = sampleWithRequiredData;
        const tailleCollection: ITaille[] = [
          {
            ...taille,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTailleToCollectionIfMissing(tailleCollection, taille);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Taille to an array that doesn't contain it", () => {
        const taille: ITaille = sampleWithRequiredData;
        const tailleCollection: ITaille[] = [sampleWithPartialData];
        expectedResult = service.addTailleToCollectionIfMissing(tailleCollection, taille);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(taille);
      });

      it('should add only unique Taille to an array', () => {
        const tailleArray: ITaille[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const tailleCollection: ITaille[] = [sampleWithRequiredData];
        expectedResult = service.addTailleToCollectionIfMissing(tailleCollection, ...tailleArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const taille: ITaille = sampleWithRequiredData;
        const taille2: ITaille = sampleWithPartialData;
        expectedResult = service.addTailleToCollectionIfMissing([], taille, taille2);
        expect(expectedResult).toEqual([taille, taille2]);
      });

      it('should accept null and undefined values', () => {
        const taille: ITaille = sampleWithRequiredData;
        expectedResult = service.addTailleToCollectionIfMissing([], null, taille, undefined);
        expect(expectedResult).toEqual([taille]);
      });

      it('should return initial array if no Taille is added', () => {
        const tailleCollection: ITaille[] = [sampleWithRequiredData];
        expectedResult = service.addTailleToCollectionIfMissing(tailleCollection, undefined, null);
        expect(expectedResult).toEqual(tailleCollection);
      });
    });

    describe('compareTaille', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTaille(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 2407 };
        const entity2 = null;

        const compareResult1 = service.compareTaille(entity1, entity2);
        const compareResult2 = service.compareTaille(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 2407 };
        const entity2 = { id: 10100 };

        const compareResult1 = service.compareTaille(entity1, entity2);
        const compareResult2 = service.compareTaille(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 2407 };
        const entity2 = { id: 2407 };

        const compareResult1 = service.compareTaille(entity1, entity2);
        const compareResult2 = service.compareTaille(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
