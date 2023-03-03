import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ResourceDataFormService, ResourceDataFormGroup } from './resource-data-form.service';
import { IResourceData } from '../resource-data.model';
import { ResourceDataService } from '../service/resource-data.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-resource-data-update',
  templateUrl: './resource-data-update.component.html',
})
export class ResourceDataUpdateComponent implements OnInit {
  isSaving = false;
  resourceData: IResourceData | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: ResourceDataFormGroup = this.resourceDataFormService.createResourceDataFormGroup();

  constructor(
    protected resourceDataService: ResourceDataService,
    protected resourceDataFormService: ResourceDataFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceData }) => {
      this.resourceData = resourceData;
      if (resourceData) {
        this.updateForm(resourceData);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resourceData = this.resourceDataFormService.getResourceData(this.editForm);
    if (resourceData.id !== null) {
      this.subscribeToSaveResponse(this.resourceDataService.update(resourceData));
    } else {
      this.subscribeToSaveResponse(this.resourceDataService.create(resourceData));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResourceData>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(resourceData: IResourceData): void {
    this.resourceData = resourceData;
    this.resourceDataFormService.resetForm(this.editForm, resourceData);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(
      this.usersSharedCollection,
      resourceData.registerUser
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.resourceData?.registerUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
