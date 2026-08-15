import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ICouleur } from '../couleur.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../couleur.test-samples';

import { CouleurService } from './couleur.service';

const requireRestSample: ICouleur = {
  ...sampleWithRequiredData,
};

describe('Couleur Service', () => {
  let service: CouleurService;
  let httpMock: HttpTestingController;
  let expectedResult: ICouleur | ICouleur[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(CouleurService);
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

    it('should create a Couleur', () => {
      const couleur = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(couleur).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Couleur', () => {
      const couleur = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(couleur).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Couleur', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Couleur', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Couleur', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addCouleurToCollectionIfMissing', () => {
      it('should add a Couleur to an empty array', () => {
        const couleur: ICouleur = sampleWithRequiredData;
        expectedResult = service.addCouleurToCollectionIfMissing([], couleur);
        expect(expectedResult).toEqual([couleur]);
      });

      it('should not add a Couleur to an array that contains it', () => {
        const couleur: ICouleur = sampleWithRequiredData;
        const couleurCollection: ICouleur[] = [
          {
            ...couleur,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addCouleurToCollectionIfMissing(couleurCollection, couleur);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Couleur to an array that doesn't contain it", () => {
        const couleur: ICouleur = sampleWithRequiredData;
        const couleurCollection: ICouleur[] = [sampleWithPartialData];
        expectedResult = service.addCouleurToCollectionIfMissing(couleurCollection, couleur);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(couleur);
      });

      it('should add only unique Couleur to an array', () => {
        const couleurArray: ICouleur[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const couleurCollection: ICouleur[] = [sampleWithRequiredData];
        expectedResult = service.addCouleurToCollectionIfMissing(couleurCollection, ...couleurArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const couleur: ICouleur = sampleWithRequiredData;
        const couleur2: ICouleur = sampleWithPartialData;
        expectedResult = service.addCouleurToCollectionIfMissing([], couleur, couleur2);
        expect(expectedResult).toEqual([couleur, couleur2]);
      });

      it('should accept null and undefined values', () => {
        const couleur: ICouleur = sampleWithRequiredData;
        expectedResult = service.addCouleurToCollectionIfMissing([], null, couleur, undefined);
        expect(expectedResult).toEqual([couleur]);
      });

      it('should return initial array if no Couleur is added', () => {
        const couleurCollection: ICouleur[] = [sampleWithRequiredData];
        expectedResult = service.addCouleurToCollectionIfMissing(couleurCollection, undefined, null);
        expect(expectedResult).toEqual(couleurCollection);
      });
    });

    describe('compareCouleur', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareCouleur(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15106 };
        const entity2 = null;

        const compareResult1 = service.compareCouleur(entity1, entity2);
        const compareResult2 = service.compareCouleur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15106 };
        const entity2 = { id: 19271 };

        const compareResult1 = service.compareCouleur(entity1, entity2);
        const compareResult2 = service.compareCouleur(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15106 };
        const entity2 = { id: 15106 };

        const compareResult1 = service.compareCouleur(entity1, entity2);
        const compareResult2 = service.compareCouleur(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
