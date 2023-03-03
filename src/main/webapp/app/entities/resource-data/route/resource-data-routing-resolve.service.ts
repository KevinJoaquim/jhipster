import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResourceData } from '../resource-data.model';
import { ResourceDataService } from '../service/resource-data.service';

@Injectable({ providedIn: 'root' })
export class ResourceDataRoutingResolveService implements Resolve<IResourceData | null> {
  constructor(protected service: ResourceDataService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResourceData | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resourceData: HttpResponse<IResourceData>) => {
          if (resourceData.body) {
            return of(resourceData.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
