import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ResourceService } from '../service/resource.service';

import { ResourceComponent } from './resource.component';

describe('Resource Management Component', () => {
  let comp: ResourceComponent;
  let fixture: ComponentFixture<ResourceComponent>;
  let service: ResourceService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'resource', component: ResourceComponent }]), HttpClientTestingModule],
      declarations: [ResourceComponent],
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
      .overrideTemplate(ResourceComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ResourceService);

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
    expect(comp.resources?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to resourceService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getResourceIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getResourceIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
