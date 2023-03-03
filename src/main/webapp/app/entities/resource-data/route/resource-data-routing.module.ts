import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ResourceDataComponent } from '../list/resource-data.component';
import { ResourceDataDetailComponent } from '../detail/resource-data-detail.component';
import { ResourceDataUpdateComponent } from '../update/resource-data-update.component';
import { ResourceDataRoutingResolveService } from './resource-data-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const resourceDataRoute: Routes = [
  {
    path: '',
    component: ResourceDataComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ResourceDataDetailComponent,
    resolve: {
      resourceData: ResourceDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ResourceDataUpdateComponent,
    resolve: {
      resourceData: ResourceDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ResourceDataUpdateComponent,
    resolve: {
      resourceData: ResourceDataRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(resourceDataRoute)],
  exports: [RouterModule],
})
export class ResourceDataRoutingModule {}
