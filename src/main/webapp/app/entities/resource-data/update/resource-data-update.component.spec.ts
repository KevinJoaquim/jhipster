import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ResourceDataFormService } from './resource-data-form.service';
import { ResourceDataService } from '../service/resource-data.service';
import { IResourceData } from '../resource-data.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ResourceDataUpdateComponent } from './resource-data-update.component';

describe('ResourceData Management Update Component', () => {
  let comp: ResourceDataUpdateComponent;
  let fixture: ComponentFixture<ResourceDataUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let resourceDataFormService: ResourceDataFormService;
  let resourceDataService: ResourceDataService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ResourceDataUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ResourceDataUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceDataUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    resourceDataFormService = TestBed.inject(ResourceDataFormService);
    resourceDataService = TestBed.inject(ResourceDataService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const resourceData: IResourceData = { id: 456 };
      const registerUser: IUser = { id: 60125 };
      resourceData.registerUser = registerUser;

      const userCollection: IUser[] = [{ id: 3612 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [registerUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ resourceData });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const resourceData: IResourceData = { id: 456 };
      const registerUser: IUser = { id: 98177 };
      resourceData.registerUser = registerUser;

      activatedRoute.data = of({ resourceData });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(registerUser);
      expect(comp.resourceData).toEqual(resourceData);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceData>>();
      const resourceData = { id: 123 };
      jest.spyOn(resourceDataFormService, 'getResourceData').mockReturnValue(resourceData);
      jest.spyOn(resourceDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceData }));
      saveSubject.complete();

      // THEN
      expect(resourceDataFormService.getResourceData).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(resourceDataService.update).toHaveBeenCalledWith(expect.objectContaining(resourceData));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceData>>();
      const resourceData = { id: 123 };
      jest.spyOn(resourceDataFormService, 'getResourceData').mockReturnValue({ id: null });
      jest.spyOn(resourceDataService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceData: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceData }));
      saveSubject.complete();

      // THEN
      expect(resourceDataFormService.getResourceData).toHaveBeenCalled();
      expect(resourceDataService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceData>>();
      const resourceData = { id: 123 };
      jest.spyOn(resourceDataService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceData });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(resourceDataService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
