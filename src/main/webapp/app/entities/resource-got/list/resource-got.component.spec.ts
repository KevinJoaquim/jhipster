import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ResourceGotService } from '../service/resource-got.service';

import { ResourceGotComponent } from './resource-got.component';

describe('ResourceGot Management Component', () => {
  let comp: ResourceGotComponent;
  let fixture: ComponentFixture<ResourceGotComponent>;
  let service: ResourceGotService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'resource-got', component: ResourceGotComponent }]), HttpClientTestingModule],
      declarations: [ResourceGotComponent],
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
      .overrideTemplate(ResourceGotComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceGotComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ResourceGotService);

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
    expect(comp.resourceGots?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to resourceGotService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getResourceGotIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getResourceGotIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
