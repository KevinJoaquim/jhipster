import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResourceGot, NewResourceGot } from '../resource-got.model';

export type PartialUpdateResourceGot = Partial<IResourceGot> & Pick<IResourceGot, 'id'>;

export type EntityResponseType = HttpResponse<IResourceGot>;
export type EntityArrayResponseType = HttpResponse<IResourceGot[]>;

@Injectable({ providedIn: 'root' })
export class ResourceGotService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/resource-gots');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resourceGot: NewResourceGot): Observable<EntityResponseType> {
    return this.http.post<IResourceGot>(this.resourceUrl, resourceGot, { observe: 'response' });
  }

  update(resourceGot: IResourceGot): Observable<EntityResponseType> {
    return this.http.put<IResourceGot>(`${this.resourceUrl}/${this.getResourceGotIdentifier(resourceGot)}`, resourceGot, {
      observe: 'response',
    });
  }

  partialUpdate(resourceGot: PartialUpdateResourceGot): Observable<EntityResponseType> {
    return this.http.patch<IResourceGot>(`${this.resourceUrl}/${this.getResourceGotIdentifier(resourceGot)}`, resourceGot, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IResourceGot>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResourceGot[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResourceGotIdentifier(resourceGot: Pick<IResourceGot, 'id'>): number {
    return resourceGot.id;
  }

  compareResourceGot(o1: Pick<IResourceGot, 'id'> | null, o2: Pick<IResourceGot, 'id'> | null): boolean {
    return o1 && o2 ? this.getResourceGotIdentifier(o1) === this.getResourceGotIdentifier(o2) : o1 === o2;
  }

  addResourceGotToCollectionIfMissing<Type extends Pick<IResourceGot, 'id'>>(
    resourceGotCollection: Type[],
    ...resourceGotsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const resourceGots: Type[] = resourceGotsToCheck.filter(isPresent);
    if (resourceGots.length > 0) {
      const resourceGotCollectionIdentifiers = resourceGotCollection.map(
        resourceGotItem => this.getResourceGotIdentifier(resourceGotItem)!
      );
      const resourceGotsToAdd = resourceGots.filter(resourceGotItem => {
        const resourceGotIdentifier = this.getResourceGotIdentifier(resourceGotItem);
        if (resourceGotCollectionIdentifiers.includes(resourceGotIdentifier)) {
          return false;
        }
        resourceGotCollectionIdentifiers.push(resourceGotIdentifier);
        return true;
      });
      return [...resourceGotsToAdd, ...resourceGotCollection];
    }
    return resourceGotCollection;
  }
}
