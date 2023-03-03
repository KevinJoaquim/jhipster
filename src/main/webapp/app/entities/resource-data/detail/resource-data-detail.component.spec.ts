import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ResourceDataDetailComponent } from './resource-data-detail.component';

describe('ResourceData Management Detail Component', () => {
  let comp: ResourceDataDetailComponent;
  let fixture: ComponentFixture<ResourceDataDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceDataDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ resourceData: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ResourceDataDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ResourceDataDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load resourceData on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.resourceData).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
