import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IResourceGot, NewResourceGot } from '../resource-got.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IResourceGot for edit and NewResourceGotFormGroupInput for create.
 */
type ResourceGotFormGroupInput = IResourceGot | PartialWithRequiredKeyOf<NewResourceGot>;

type ResourceGotFormDefaults = Pick<NewResourceGot, 'id'>;

type ResourceGotFormGroupContent = {
  id: FormControl<IResourceGot['id'] | NewResourceGot['id']>;
  gold: FormControl<IResourceGot['gold']>;
  wood: FormControl<IResourceGot['wood']>;
  fer: FormControl<IResourceGot['fer']>;
  registerUser: FormControl<IResourceGot['registerUser']>;
};

export type ResourceGotFormGroup = FormGroup<ResourceGotFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class ResourceGotFormService {
  createResourceGotFormGroup(resourceGot: ResourceGotFormGroupInput = { id: null }): ResourceGotFormGroup {
    const resourceGotRawValue = {
      ...this.getFormDefaults(),
      ...resourceGot,
    };
    return new FormGroup<ResourceGotFormGroupContent>({
      id: new FormControl(
        { value: resourceGotRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      gold: new FormControl(resourceGotRawValue.gold),
      wood: new FormControl(resourceGotRawValue.wood),
      fer: new FormControl(resourceGotRawValue.fer),
      registerUser: new FormControl(resourceGotRawValue.registerUser),
    });
  }

  getResourceGot(form: ResourceGotFormGroup): IResourceGot | NewResourceGot {
    return form.getRawValue() as IResourceGot | NewResourceGot;
  }

  resetForm(form: ResourceGotFormGroup, resourceGot: ResourceGotFormGroupInput): void {
    const resourceGotRawValue = { ...this.getFormDefaults(), ...resourceGot };
    form.reset(
      {
        ...resourceGotRawValue,
        id: { value: resourceGotRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): ResourceGotFormDefaults {
    return {
      id: null,
    };
  }
}
