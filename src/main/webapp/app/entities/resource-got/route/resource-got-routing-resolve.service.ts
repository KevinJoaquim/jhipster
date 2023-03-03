import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResourceGot } from '../resource-got.model';
import { ResourceGotService } from '../service/resource-got.service';

@Injectable({ providedIn: 'root' })
export class ResourceGotRoutingResolveService implements Resolve<IResourceGot | null> {
  constructor(protected service: ResourceGotService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IResourceGot | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resourceGot: HttpResponse<IResourceGot>) => {
          if (resourceGot.body) {
            return of(resourceGot.body);
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
