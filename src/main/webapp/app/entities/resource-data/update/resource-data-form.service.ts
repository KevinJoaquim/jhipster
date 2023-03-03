import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IResourceData, NewResourceData } from '../resource-data.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResourceData for edit and NewResourceDataFormGroupInput for create.
 */
type ResourceDataFormGroupInput = IResourceData | PartialWithRequiredKeyOf<NewResourceData>;

type ResourceDataFormDefaults = Pick<NewResourceData, 'id'>;

type ResourceDataFormGroupContent = {
  id: FormControl<IResourceData['id'] | NewResourceData['id']>;
  gold: FormControl<IResourceData['gold']>;
  wood: FormControl<IResourceData['wood']>;
  fer: FormControl<IResourceData['fer']>;
  registerUser: FormControl<IResourceData['registerUser']>;
};

export type ResourceDataFormGroup = FormGroup<ResourceDataFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResourceDataFormService {
  createResourceDataFormGroup(resourceData: ResourceDataFormGroupInput = { id: null }): ResourceDataFormGroup {
    const resourceDataRawValue = {
      ...this.getFormDefaults(),
      ...resourceData,
    };
    return new FormGroup<ResourceDataFormGroupContent>({
      id: new FormControl(
        { value: resourceDataRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      gold: new FormControl(resourceDataRawValue.gold),
      wood: new FormControl(resourceDataRawValue.wood),
      fer: new FormControl(resourceDataRawValue.fer),
      registerUser: new FormControl(resourceDataRawValue.registerUser),
    });
  }

  getResourceData(form: ResourceDataFormGroup): IResourceData | NewResourceData {
    return form.getRawValue() as IResourceData | NewResourceData;
  }

  resetForm(form: ResourceDataFormGroup, resourceData: ResourceDataFormGroupInput): void {
    const resourceDataRawValue = { ...this.getFormDefaults(), ...resourceData };
    form.reset(
      {
        ...resourceDataRawValue,
        id: { value: resourceDataRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ResourceDataFormDefaults {
    return {
      id: null,
    };
  }
}
