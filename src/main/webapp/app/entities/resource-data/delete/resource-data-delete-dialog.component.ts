import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IResourceData } from '../resource-data.model';
import { ResourceDataService } from '../service/resource-data.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './resource-data-delete-dialog.component.html',
})
export class ResourceDataDeleteDialogComponent {
  resourceData?: IResourceData;

  constructor(protected resourceDataService: ResourceDataService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.resourceDataService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
