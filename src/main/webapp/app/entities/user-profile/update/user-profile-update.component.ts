import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { UserProfileFormService, UserProfileFormGroup } from './user-profile-form.service';
import { IUserProfile } from '../user-profile.model';
import { UserProfileService } from '../service/user-profile.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IResourceData } from 'app/entities/resource-data/resource-data.model';
import { ResourceDataService } from 'app/entities/resource-data/service/resource-data.service';

@Component({
  selector: 'jhi-user-profile-update',
  templateUrl: './user-profile-update.component.html',
})
export class UserProfileUpdateComponent implements OnInit {
  isSaving = false;
  userProfile: IUserProfile | null = null;

  usersSharedCollection: IUser[] = [];
  resourceDataSharedCollection: IResourceData[] = [];

  editForm: UserProfileFormGroup = this.userProfileFormService.createUserProfileFormGroup();

  constructor(
    protected userProfileService: UserProfileService,
    protected userProfileFormService: UserProfileFormService,
    protected userService: UserService,
    protected resourceDataService: ResourceDataService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareResourceData = (o1: IResourceData | null, o2: IResourceData | null): boolean =>
    this.resourceDataService.compareResourceData(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ userProfile }) => {
      this.userProfile = userProfile;
      if (userProfile) {
        this.updateForm(userProfile);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const userProfile = this.userProfileFormService.getUserProfile(this.editForm);
    if (userProfile.id !== null) {
      this.subscribeToSaveResponse(this.userProfileService.update(userProfile));
    } else {
      this.subscribeToSaveResponse(this.userProfileService.create(userProfile));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IUserProfile>>): void {
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

  protected updateForm(userProfile: IUserProfile): void {
    this.userProfile = userProfile;
    this.userProfileFormService.resetForm(this.editForm, userProfile);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, userProfile.user);
    this.resourceDataSharedCollection = this.resourceDataService.addResourceDataToCollectionIfMissing<IResourceData>(
      this.resourceDataSharedCollection,
      userProfile.resource
    );
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.userProfile?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.resourceDataService
      .query()
      .pipe(map((res: HttpResponse<IResourceData[]>) => res.body ?? []))
      .pipe(
        map((resourceData: IResourceData[]) =>
          this.resourceDataService.addResourceDataToCollectionIfMissing<IResourceData>(resourceData, this.userProfile?.resource)
        )
      )
      .subscribe((resourceData: IResourceData[]) => (this.resourceDataSharedCollection = resourceData));
  }
}
