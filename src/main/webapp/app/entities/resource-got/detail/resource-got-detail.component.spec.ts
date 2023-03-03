import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ResourceGotDetailComponent } from './resource-got-detail.component';

describe('ResourceGot Management Detail Component', () => {
  let comp: ResourceGotDetailComponent;
  let fixture: ComponentFixture<ResourceGotDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ResourceGotDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ resourceGot: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ResourceGotDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ResourceGotDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load resourceGot on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.resourceGot).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
