import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ResourceGotComponent } from '../list/resource-got.component';
import { ResourceGotDetailComponent } from '../detail/resource-got-detail.component';
import { ResourceGotUpdateComponent } from '../update/resource-got-update.component';
import { ResourceGotRoutingResolveService } from './resource-got-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const resourceGotRoute: Routes = [
  {
    path: '',
    component: ResourceGotComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ResourceGotDetailComponent,
    resolve: {
      resourceGot: ResourceGotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ResourceGotUpdateComponent,
    resolve: {
      resourceGot: ResourceGotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ResourceGotUpdateComponent,
    resolve: {
      resourceGot: ResourceGotRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(resourceGotRoute)],
  exports: [RouterModule],
})
export class ResourceGotRoutingModule {}
