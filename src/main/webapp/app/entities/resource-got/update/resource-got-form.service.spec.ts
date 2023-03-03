import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../resource-got.test-samples';

import { ResourceGotFormService } from './resource-got-form.service';

describe('ResourceGot Form Service', () => {
  let service: ResourceGotFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResourceGotFormService);
  });

  describe('Service methods', () => {
    describe('createResourceGotFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createResourceGotFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            gold: expect.any(Object),
            wood: expect.any(Object),
            fer: expect.any(Object),
            registerUser: expect.any(Object),
          })
        );
      });

      it('passing IResourceGot should create a new form with FormGroup', () => {
        const formGroup = service.createResourceGotFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            gold: expect.any(Object),
            wood: expect.any(Object),
            fer: expect.any(Object),
            registerUser: expect.any(Object),
          })
        );
      });
    });

    describe('getResourceGot', () => {
      it('should return NewResourceGot for default ResourceGot initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createResourceGotFormGroup(sampleWithNewData);

        const resourceGot = service.getResourceGot(formGroup) as any;

        expect(resourceGot).toMatchObject(sampleWithNewData);
      });

      it('should return NewResourceGot for empty ResourceGot initial value', () => {
        const formGroup = service.createResourceGotFormGroup();

        const resourceGot = service.getResourceGot(formGroup) as any;

        expect(resourceGot).toMatchObject({});
      });

      it('should return IResourceGot', () => {
        const formGroup = service.createResourceGotFormGroup(sampleWithRequiredData);

        const resourceGot = service.getResourceGot(formGroup) as any;

        expect(resourceGot).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IResourceGot should not enable id FormControl', () => {
        const formGroup = service.createResourceGotFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewResourceGot should disable id FormControl', () => {
        const formGroup = service.createResourceGotFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
