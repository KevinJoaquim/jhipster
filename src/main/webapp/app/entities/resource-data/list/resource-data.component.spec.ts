import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ResourceDataService } from '../service/resource-data.service';

import { ResourceDataComponent } from './resource-data.component';

describe('ResourceData Management Component', () => {
  let comp: ResourceDataComponent;
  let fixture: ComponentFixture<ResourceDataComponent>;
  let service: ResourceDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'resource-data', component: ResourceDataComponent }]), HttpClientTestingModule],
      declarations: [ResourceDataComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ResourceDataComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceDataComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ResourceDataService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.resourceData?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to resourceDataService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getResourceDataIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getResourceDataIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
