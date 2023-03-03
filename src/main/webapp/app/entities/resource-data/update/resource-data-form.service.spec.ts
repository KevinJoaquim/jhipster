import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../resource-data.test-samples';

import { ResourceDataFormService } from './resource-data-form.service';

describe('ResourceData Form Service', () => {
  let service: ResourceDataFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResourceDataFormService);
  });

  describe('Service methods', () => {
    describe('createResourceDataFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createResourceDataFormGroup();

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

      it('passing IResourceData should create a new form with FormGroup', () => {
        const formGroup = service.createResourceDataFormGroup(sampleWithRequiredData);

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

    describe('getResourceData', () => {
      it('should return NewResourceData for default ResourceData initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createResourceDataFormGroup(sampleWithNewData);

        const resourceData = service.getResourceData(formGroup) as any;

        expect(resourceData).toMatchObject(sampleWithNewData);
      });

      it('should return NewResourceData for empty ResourceData initial value', () => {
        const formGroup = service.createResourceDataFormGroup();

        const resourceData = service.getResourceData(formGroup) as any;

        expect(resourceData).toMatchObject({});
      });

      it('should return IResourceData', () => {
        const formGroup = service.createResourceDataFormGroup(sampleWithRequiredData);

        const resourceData = service.getResourceData(formGroup) as any;

        expect(resourceData).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IResourceData should not enable id FormControl', () => {
        const formGroup = service.createResourceDataFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewResourceData should disable id FormControl', () => {
        const formGroup = service.createResourceDataFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
