import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ResourceDataComponent } from './list/resource-data.component';
import { ResourceDataDetailComponent } from './detail/resource-data-detail.component';
import { ResourceDataUpdateComponent } from './update/resource-data-update.component';
import { ResourceDataDeleteDialogComponent } from './delete/resource-data-delete-dialog.component';
import { ResourceDataRoutingModule } from './route/resource-data-routing.module';

@NgModule({
  imports: [SharedModule, ResourceDataRoutingModule],
  declarations: [ResourceDataComponent, ResourceDataDetailComponent, ResourceDataUpdateComponent, ResourceDataDeleteDialogComponent],
})
export class ResourceDataModule {}
