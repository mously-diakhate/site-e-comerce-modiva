import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IImageProduit } from '../image-produit.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../image-produit.test-samples';

import { ImageProduitService } from './image-produit.service';

const requireRestSample: IImageProduit = {
  ...sampleWithRequiredData,
};

describe('ImageProduit Service', () => {
  let service: ImageProduitService;
  let httpMock: HttpTestingController;
  let expectedResult: IImageProduit | IImageProduit[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ImageProduitService);
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

    it('should create a ImageProduit', () => {
      const imageProduit = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(imageProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ImageProduit', () => {
      const imageProduit = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(imageProduit).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ImageProduit', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ImageProduit', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ImageProduit', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addImageProduitToCollectionIfMissing', () => {
      it('should add a ImageProduit to an empty array', () => {
        const imageProduit: IImageProduit = sampleWithRequiredData;
        expectedResult = service.addImageProduitToCollectionIfMissing([], imageProduit);
        expect(expectedResult).toEqual([imageProduit]);
      });

      it('should not add a ImageProduit to an array that contains it', () => {
        const imageProduit: IImageProduit = sampleWithRequiredData;
        const imageProduitCollection: IImageProduit[] = [
          {
            ...imageProduit,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addImageProduitToCollectionIfMissing(imageProduitCollection, imageProduit);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ImageProduit to an array that doesn't contain it", () => {
        const imageProduit: IImageProduit = sampleWithRequiredData;
        const imageProduitCollection: IImageProduit[] = [sampleWithPartialData];
        expectedResult = service.addImageProduitToCollectionIfMissing(imageProduitCollection, imageProduit);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(imageProduit);
      });

      it('should add only unique ImageProduit to an array', () => {
        const imageProduitArray: IImageProduit[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const imageProduitCollection: IImageProduit[] = [sampleWithRequiredData];
        expectedResult = service.addImageProduitToCollectionIfMissing(imageProduitCollection, ...imageProduitArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const imageProduit: IImageProduit = sampleWithRequiredData;
        const imageProduit2: IImageProduit = sampleWithPartialData;
        expectedResult = service.addImageProduitToCollectionIfMissing([], imageProduit, imageProduit2);
        expect(expectedResult).toEqual([imageProduit, imageProduit2]);
      });

      it('should accept null and undefined values', () => {
        const imageProduit: IImageProduit = sampleWithRequiredData;
        expectedResult = service.addImageProduitToCollectionIfMissing([], null, imageProduit, undefined);
        expect(expectedResult).toEqual([imageProduit]);
      });

      it('should return initial array if no ImageProduit is added', () => {
        const imageProduitCollection: IImageProduit[] = [sampleWithRequiredData];
        expectedResult = service.addImageProduitToCollectionIfMissing(imageProduitCollection, undefined, null);
        expect(expectedResult).toEqual(imageProduitCollection);
      });
    });

    describe('compareImageProduit', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareImageProduit(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 15619 };
        const entity2 = null;

        const compareResult1 = service.compareImageProduit(entity1, entity2);
        const compareResult2 = service.compareImageProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 15619 };
        const entity2 = { id: 204 };

        const compareResult1 = service.compareImageProduit(entity1, entity2);
        const compareResult2 = service.compareImageProduit(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 15619 };
        const entity2 = { id: 15619 };

        const compareResult1 = service.compareImageProduit(entity1, entity2);
        const compareResult2 = service.compareImageProduit(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
