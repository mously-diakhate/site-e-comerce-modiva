import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ILigneCommande } from '../ligne-commande.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../ligne-commande.test-samples';

import { LigneCommandeService } from './ligne-commande.service';

const requireRestSample: ILigneCommande = {
  ...sampleWithRequiredData,
};

describe('LigneCommande Service', () => {
  let service: LigneCommandeService;
  let httpMock: HttpTestingController;
  let expectedResult: ILigneCommande | ILigneCommande[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(LigneCommandeService);
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

    it('should create a LigneCommande', () => {
      const ligneCommande = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(ligneCommande).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a LigneCommande', () => {
      const ligneCommande = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(ligneCommande).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a LigneCommande', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of LigneCommande', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a LigneCommande', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addLigneCommandeToCollectionIfMissing', () => {
      it('should add a LigneCommande to an empty array', () => {
        const ligneCommande: ILigneCommande = sampleWithRequiredData;
        expectedResult = service.addLigneCommandeToCollectionIfMissing([], ligneCommande);
        expect(expectedResult).toEqual([ligneCommande]);
      });

      it('should not add a LigneCommande to an array that contains it', () => {
        const ligneCommande: ILigneCommande = sampleWithRequiredData;
        const ligneCommandeCollection: ILigneCommande[] = [
          {
            ...ligneCommande,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addLigneCommandeToCollectionIfMissing(ligneCommandeCollection, ligneCommande);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a LigneCommande to an array that doesn't contain it", () => {
        const ligneCommande: ILigneCommande = sampleWithRequiredData;
        const ligneCommandeCollection: ILigneCommande[] = [sampleWithPartialData];
        expectedResult = service.addLigneCommandeToCollectionIfMissing(ligneCommandeCollection, ligneCommande);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(ligneCommande);
      });

      it('should add only unique LigneCommande to an array', () => {
        const ligneCommandeArray: ILigneCommande[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const ligneCommandeCollection: ILigneCommande[] = [sampleWithRequiredData];
        expectedResult = service.addLigneCommandeToCollectionIfMissing(ligneCommandeCollection, ...ligneCommandeArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const ligneCommande: ILigneCommande = sampleWithRequiredData;
        const ligneCommande2: ILigneCommande = sampleWithPartialData;
        expectedResult = service.addLigneCommandeToCollectionIfMissing([], ligneCommande, ligneCommande2);
        expect(expectedResult).toEqual([ligneCommande, ligneCommande2]);
      });

      it('should accept null and undefined values', () => {
        const ligneCommande: ILigneCommande = sampleWithRequiredData;
        expectedResult = service.addLigneCommandeToCollectionIfMissing([], null, ligneCommande, undefined);
        expect(expectedResult).toEqual([ligneCommande]);
      });

      it('should return initial array if no LigneCommande is added', () => {
        const ligneCommandeCollection: ILigneCommande[] = [sampleWithRequiredData];
        expectedResult = service.addLigneCommandeToCollectionIfMissing(ligneCommandeCollection, undefined, null);
        expect(expectedResult).toEqual(ligneCommandeCollection);
      });
    });

    describe('compareLigneCommande', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareLigneCommande(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 21869 };
        const entity2 = null;

        const compareResult1 = service.compareLigneCommande(entity1, entity2);
        const compareResult2 = service.compareLigneCommande(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 21869 };
        const entity2 = { id: 4281 };

        const compareResult1 = service.compareLigneCommande(entity1, entity2);
        const compareResult2 = service.compareLigneCommande(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 21869 };
        const entity2 = { id: 21869 };

        const compareResult1 = service.compareLigneCommande(entity1, entity2);
        const compareResult2 = service.compareLigneCommande(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
