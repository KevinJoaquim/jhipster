import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IResourceGot } from '../resource-got.model';
import { ResourceGotService } from '../service/resource-got.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './resource-got-delete-dialog.component.html',
})
export class ResourceGotDeleteDialogComponent {
  resourceGot?: IResourceGot;

  constructor(protected resourceGotService: ResourceGotService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.resourceGotService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
