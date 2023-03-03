import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ResourceGotFormService, ResourceGotFormGroup } from './resource-got-form.service';
import { IResourceGot } from '../resource-got.model';
import { ResourceGotService } from '../service/resource-got.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-resource-got-update',
  templateUrl: './resource-got-update.component.html',
})
export class ResourceGotUpdateComponent implements OnInit {
  isSaving = false;
  resourceGot: IResourceGot | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: ResourceGotFormGroup = this.resourceGotFormService.createResourceGotFormGroup();

  constructor(
    protected resourceGotService: ResourceGotService,
    protected resourceGotFormService: ResourceGotFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resourceGot }) => {
      this.resourceGot = resourceGot;
      if (resourceGot) {
        this.updateForm(resourceGot);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resourceGot = this.resourceGotFormService.getResourceGot(this.editForm);
    if (resourceGot.id !== null) {
      this.subscribeToSaveResponse(this.resourceGotService.update(resourceGot));
    } else {
      this.subscribeToSaveResponse(this.resourceGotService.create(resourceGot));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResourceGot>>): void {
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

  protected updateForm(resourceGot: IResourceGot): void {
    this.resourceGot = resourceGot;
    this.resourceGotFormService.resetForm(this.editForm, resourceGot);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, resourceGot.registerUser);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.resourceGot?.registerUser)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
