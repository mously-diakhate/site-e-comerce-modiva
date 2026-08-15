import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IAdresse } from '../adresse.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../adresse.test-samples';

import { AdresseService } from './adresse.service';

const requireRestSample: IAdresse = {
  ...sampleWithRequiredData,
};

describe('Adresse Service', () => {
  let service: AdresseService;
  let httpMock: HttpTestingController;
  let expectedResult: IAdresse | IAdresse[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(AdresseService);
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

    it('should create a Adresse', () => {
      const adresse = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(adresse).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Adresse', () => {
      const adresse = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(adresse).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Adresse', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Adresse', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Adresse', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addAdresseToCollectionIfMissing', () => {
      it('should add a Adresse to an empty array', () => {
        const adresse: IAdresse = sampleWithRequiredData;
        expectedResult = service.addAdresseToCollectionIfMissing([], adresse);
        expect(expectedResult).toEqual([adresse]);
      });

      it('should not add a Adresse to an array that contains it', () => {
        const adresse: IAdresse = sampleWithRequiredData;
        const adresseCollection: IAdresse[] = [
          {
            ...adresse,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addAdresseToCollectionIfMissing(adresseCollection, adresse);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Adresse to an array that doesn't contain it", () => {
        const adresse: IAdresse = sampleWithRequiredData;
        const adresseCollection: IAdresse[] = [sampleWithPartialData];
        expectedResult = service.addAdresseToCollectionIfMissing(adresseCollection, adresse);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(adresse);
      });

      it('should add only unique Adresse to an array', () => {
        const adresseArray: IAdresse[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const adresseCollection: IAdresse[] = [sampleWithRequiredData];
        expectedResult = service.addAdresseToCollectionIfMissing(adresseCollection, ...adresseArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const adresse: IAdresse = sampleWithRequiredData;
        const adresse2: IAdresse = sampleWithPartialData;
        expectedResult = service.addAdresseToCollectionIfMissing([], adresse, adresse2);
        expect(expectedResult).toEqual([adresse, adresse2]);
      });

      it('should accept null and undefined values', () => {
        const adresse: IAdresse = sampleWithRequiredData;
        expectedResult = service.addAdresseToCollectionIfMissing([], null, adresse, undefined);
        expect(expectedResult).toEqual([adresse]);
      });

      it('should return initial array if no Adresse is added', () => {
        const adresseCollection: IAdresse[] = [sampleWithRequiredData];
        expectedResult = service.addAdresseToCollectionIfMissing(adresseCollection, undefined, null);
        expect(expectedResult).toEqual(adresseCollection);
      });
    });

    describe('compareAdresse', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareAdresse(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 31795 };
        const entity2 = null;

        const compareResult1 = service.compareAdresse(entity1, entity2);
        const compareResult2 = service.compareAdresse(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 31795 };
        const entity2 = { id: 16957 };

        const compareResult1 = service.compareAdresse(entity1, entity2);
        const compareResult2 = service.compareAdresse(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 31795 };
        const entity2 = { id: 31795 };

        const compareResult1 = service.compareAdresse(entity1, entity2);
        const compareResult2 = service.compareAdresse(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
