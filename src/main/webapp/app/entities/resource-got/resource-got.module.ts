import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ResourceGotComponent } from './list/resource-got.component';
import { ResourceGotDetailComponent } from './detail/resource-got-detail.component';
import { ResourceGotUpdateComponent } from './update/resource-got-update.component';
import { ResourceGotDeleteDialogComponent } from './delete/resource-got-delete-dialog.component';
import { ResourceGotRoutingModule } from './route/resource-got-routing.module';

@NgModule({
  imports: [SharedModule, ResourceGotRoutingModule],
  declarations: [ResourceGotComponent, ResourceGotDetailComponent, ResourceGotUpdateComponent, ResourceGotDeleteDialogComponent],
})
export class ResourceGotModule {}
