import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ResourceFormService, ResourceFormGroup } from './resource-form.service';
import { IResource } from '../resource.model';
import { ResourceService } from '../service/resource.service';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

@Component({
  selector: 'jhi-resource-update',
  templateUrl: './resource-update.component.html',
})
export class ResourceUpdateComponent implements OnInit {
  isSaving = false;
  resource: IResource | null = null;

  clientsSharedCollection: IClient[] = [];

  editForm: ResourceFormGroup = this.resourceFormService.createResourceFormGroup();

  constructor(
    protected resourceService: ResourceService,
    protected resourceFormService: ResourceFormService,
    protected clientService: ClientService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareClient = (o1: IClient | null, o2: IClient | null): boolean => this.clientService.compareClient(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resource }) => {
      this.resource = resource;
      if (resource) {
        this.updateForm(resource);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resource = this.resourceFormService.getResource(this.editForm);
    if (resource.id !== null) {
      this.subscribeToSaveResponse(this.resourceService.update(resource));
    } else {
      this.subscribeToSaveResponse(this.resourceService.create(resource));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResource>>): void {
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

  protected updateForm(resource: IResource): void {
    this.resource = resource;
    this.resourceFormService.resetForm(this.editForm, resource);

    this.clientsSharedCollection = this.clientService.addClientToCollectionIfMissing<IClient>(
      this.clientsSharedCollection,
      resource.client
    );
  }

  protected loadRelationshipsOptions(): void {
    this.clientService
      .query()
      .pipe(map((res: HttpResponse<IClient[]>) => res.body ?? []))
      .pipe(map((clients: IClient[]) => this.clientService.addClientToCollectionIfMissing<IClient>(clients, this.resource?.client)))
      .subscribe((clients: IClient[]) => (this.clientsSharedCollection = clients));
  }
}
