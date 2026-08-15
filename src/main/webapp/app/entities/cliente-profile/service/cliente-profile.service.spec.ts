import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { IClienteProfile } from '../cliente-profile.model';
import { sampleWithFullData, sampleWithNewData, sampleWithPartialData, sampleWithRequiredData } from '../cliente-profile.test-samples';

import { ClienteProfileService } from './cliente-profile.service';

const requireRestSample: IClienteProfile = {
  ...sampleWithRequiredData,
};

describe('ClienteProfile Service', () => {
  let service: ClienteProfileService;
  let httpMock: HttpTestingController;
  let expectedResult: IClienteProfile | IClienteProfile[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClientTesting()],
    });
    expectedResult = null;
    service = TestBed.inject(ClienteProfileService);
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

    it('should create a ClienteProfile', () => {
      const clienteProfile = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(clienteProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ClienteProfile', () => {
      const clienteProfile = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(clienteProfile).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ClienteProfile', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ClienteProfile', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ClienteProfile', () => {
      service.delete(123).subscribe();

      const requests = httpMock.match({ method: 'DELETE' });
      expect(requests.length).toBe(1);
    });

    describe('addClienteProfileToCollectionIfMissing', () => {
      it('should add a ClienteProfile to an empty array', () => {
        const clienteProfile: IClienteProfile = sampleWithRequiredData;
        expectedResult = service.addClienteProfileToCollectionIfMissing([], clienteProfile);
        expect(expectedResult).toEqual([clienteProfile]);
      });

      it('should not add a ClienteProfile to an array that contains it', () => {
        const clienteProfile: IClienteProfile = sampleWithRequiredData;
        const clienteProfileCollection: IClienteProfile[] = [
          {
            ...clienteProfile,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addClienteProfileToCollectionIfMissing(clienteProfileCollection, clienteProfile);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ClienteProfile to an array that doesn't contain it", () => {
        const clienteProfile: IClienteProfile = sampleWithRequiredData;
        const clienteProfileCollection: IClienteProfile[] = [sampleWithPartialData];
        expectedResult = service.addClienteProfileToCollectionIfMissing(clienteProfileCollection, clienteProfile);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(clienteProfile);
      });

      it('should add only unique ClienteProfile to an array', () => {
        const clienteProfileArray: IClienteProfile[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const clienteProfileCollection: IClienteProfile[] = [sampleWithRequiredData];
        expectedResult = service.addClienteProfileToCollectionIfMissing(clienteProfileCollection, ...clienteProfileArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const clienteProfile: IClienteProfile = sampleWithRequiredData;
        const clienteProfile2: IClienteProfile = sampleWithPartialData;
        expectedResult = service.addClienteProfileToCollectionIfMissing([], clienteProfile, clienteProfile2);
        expect(expectedResult).toEqual([clienteProfile, clienteProfile2]);
      });

      it('should accept null and undefined values', () => {
        const clienteProfile: IClienteProfile = sampleWithRequiredData;
        expectedResult = service.addClienteProfileToCollectionIfMissing([], null, clienteProfile, undefined);
        expect(expectedResult).toEqual([clienteProfile]);
      });

      it('should return initial array if no ClienteProfile is added', () => {
        const clienteProfileCollection: IClienteProfile[] = [sampleWithRequiredData];
        expectedResult = service.addClienteProfileToCollectionIfMissing(clienteProfileCollection, undefined, null);
        expect(expectedResult).toEqual(clienteProfileCollection);
      });
    });

    describe('compareClienteProfile', () => {
      it('should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareClienteProfile(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('should return false if one entity is null', () => {
        const entity1 = { id: 4325 };
        const entity2 = null;

        const compareResult1 = service.compareClienteProfile(entity1, entity2);
        const compareResult2 = service.compareClienteProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey differs', () => {
        const entity1 = { id: 4325 };
        const entity2 = { id: 25314 };

        const compareResult1 = service.compareClienteProfile(entity1, entity2);
        const compareResult2 = service.compareClienteProfile(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('should return false if primaryKey matches', () => {
        const entity1 = { id: 4325 };
        const entity2 = { id: 4325 };

        const compareResult1 = service.compareClienteProfile(entity1, entity2);
        const compareResult2 = service.compareClienteProfile(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
