import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'resource-data',
        data: { pageTitle: 'myappApp.resourceData.home.title' },
        loadChildren: () => import('./resource-data/resource-data.module').then(m => m.ResourceDataModule),
      },
      {
        path: 'client',
        data: { pageTitle: 'myappApp.client.home.title' },
        loadChildren: () => import('./client/client.module').then(m => m.ClientModule),
      },
      {
        path: 'resource',
        data: { pageTitle: 'myappApp.resource.home.title' },
        loadChildren: () => import('./resource/resource.module').then(m => m.ResourceModule),
      },
      {
        path: 'user-profile',
        data: { pageTitle: 'myappApp.userProfile.home.title' },
        loadChildren: () => import('./user-profile/user-profile.module').then(m => m.UserProfileModule),
      },
      {
        path: 'resource-got',
        data: { pageTitle: 'myappApp.resourceGot.home.title' },
        loadChildren: () => import('./resource-got/resource-got.module').then(m => m.ResourceGotModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
