import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IResourceGot } from '../resource-got.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../resource-got.test-samples';

import { ResourceGotService } from './resource-got.service';

const requireRestSample: IResourceGot = {
  ...sampleWithRequiredData,
};

describe('ResourceGot Service', () => {
  let service: ResourceGotService;
  let httpMock: HttpTestingController;
  let expectedResult: IResourceGot | IResourceGot[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ResourceGotService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a ResourceGot', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const resourceGot = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(resourceGot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ResourceGot', () => {
      const resourceGot = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(resourceGot).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ResourceGot', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ResourceGot', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ResourceGot', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addResourceGotToCollectionIfMissing', () => {
      it('should add a ResourceGot to an empty array', () => {
        const resourceGot: IResourceGot = sampleWithRequiredData;
        expectedResult = service.addResourceGotToCollectionIfMissing([], resourceGot);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(resourceGot);
      });

      it('should not add a ResourceGot to an array that contains it', () => {
        const resourceGot: IResourceGot = sampleWithRequiredData;
        const resourceGotCollection: IResourceGot[] = [
          {
            ...resourceGot,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addResourceGotToCollectionIfMissing(resourceGotCollection, resourceGot);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ResourceGot to an array that doesn't contain it", () => {
        const resourceGot: IResourceGot = sampleWithRequiredData;
        const resourceGotCollection: IResourceGot[] = [sampleWithPartialData];
        expectedResult = service.addResourceGotToCollectionIfMissing(resourceGotCollection, resourceGot);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(resourceGot);
      });

      it('should add only unique ResourceGot to an array', () => {
        const resourceGotArray: IResourceGot[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const resourceGotCollection: IResourceGot[] = [sampleWithRequiredData];
        expectedResult = service.addResourceGotToCollectionIfMissing(resourceGotCollection, ...resourceGotArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const resourceGot: IResourceGot = sampleWithRequiredData;
        const resourceGot2: IResourceGot = sampleWithPartialData;
        expectedResult = service.addResourceGotToCollectionIfMissing([], resourceGot, resourceGot2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(resourceGot);
        expect(expectedResult).toContain(resourceGot2);
      });

      it('should accept null and undefined values', () => {
        const resourceGot: IResourceGot = sampleWithRequiredData;
        expectedResult = service.addResourceGotToCollectionIfMissing([], null, resourceGot, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(resourceGot);
      });

      it('should return initial array if no ResourceGot is added', () => {
        const resourceGotCollection: IResourceGot[] = [sampleWithRequiredData];
        expectedResult = service.addResourceGotToCollectionIfMissing(resourceGotCollection, undefined, null);
        expect(expectedResult).toEqual(resourceGotCollection);
      });
    });

    describe('compareResourceGot', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareResourceGot(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareResourceGot(entity1, entity2);
        const compareResult2 = service.compareResourceGot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareResourceGot(entity1, entity2);
        const compareResult2 = service.compareResourceGot(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareResourceGot(entity1, entity2);
        const compareResult2 = service.compareResourceGot(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
