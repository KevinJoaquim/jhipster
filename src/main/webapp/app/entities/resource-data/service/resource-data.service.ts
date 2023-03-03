import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IResourceData, NewResourceData } from '../resource-data.model';

export type PartialUpdateResourceData = Partial<IResourceData> & Pick<IResourceData, 'id'>;

export type EntityResponseType = HttpResponse<IResourceData>;
export type EntityArrayResponseType = HttpResponse<IResourceData[]>;

@Injectable({ providedIn: 'root' })
export class ResourceDataService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/resource-data');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(resourceData: NewResourceData): Observable<EntityResponseType> {
    return this.http.post<IResourceData>(this.resourceUrl, resourceData, { observe: 'response' });
  }

  update(resourceData: IResourceData): Observable<EntityResponseType> {
    return this.http.put<IResourceData>(`${this.resourceUrl}/${this.getResourceDataIdentifier(resourceData)}`, resourceData, {
      observe: 'response',
    });
  }

  partialUpdate(resourceData: PartialUpdateResourceData): Observable<EntityResponseType> {
    return this.http.patch<IResourceData>(`${this.resourceUrl}/${this.getResourceDataIdentifier(resourceData)}`, resourceData, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IResourceData>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IResourceData[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getResourceDataIdentifier(resourceData: Pick<IResourceData, 'id'>): number {
    return resourceData.id;
  }

  compareResourceData(o1: Pick<IResourceData, 'id'> | null, o2: Pick<IResourceData, 'id'> | null): boolean {
    return o1 && o2 ? this.getResourceDataIdentifier(o1) === this.getResourceDataIdentifier(o2) : o1 === o2;
  }

  addResourceDataToCollectionIfMissing<Type extends Pick<IResourceData, 'id'>>(
    resourceDataCollection: Type[],
    ...resourceDataToCheck: (Type | null | undefined)[]
  ): Type[] {
    const resourceData: Type[] = resourceDataToCheck.filter(isPresent);
    if (resourceData.length > 0) {
      const resourceDataCollectionIdentifiers = resourceDataCollection.map(
        resourceDataItem => this.getResourceDataIdentifier(resourceDataItem)!
      );
      const resourceDataToAdd = resourceData.filter(resourceDataItem => {
        const resourceDataIdentifier = this.getResourceDataIdentifier(resourceDataItem);
        if (resourceDataCollectionIdentifiers.includes(resourceDataIdentifier)) {
          return false;
        }
        resourceDataCollectionIdentifiers.push(resourceDataIdentifier);
        return true;
      });
      return [...resourceDataToAdd, ...resourceDataCollection];
    }
    return resourceDataCollection;
  }
}
