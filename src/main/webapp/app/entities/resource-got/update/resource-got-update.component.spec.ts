import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ResourceGotFormService } from './resource-got-form.service';
import { ResourceGotService } from '../service/resource-got.service';
import { IResourceGot } from '../resource-got.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { ResourceGotUpdateComponent } from './resource-got-update.component';

describe('ResourceGot Management Update Component', () => {
  let comp: ResourceGotUpdateComponent;
  let fixture: ComponentFixture<ResourceGotUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let resourceGotFormService: ResourceGotFormService;
  let resourceGotService: ResourceGotService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ResourceGotUpdateComponent],
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
      .overrideTemplate(ResourceGotUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceGotUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    resourceGotFormService = TestBed.inject(ResourceGotFormService);
    resourceGotService = TestBed.inject(ResourceGotService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const resourceGot: IResourceGot = { id: 456 };
      const registerUser: IUser = { id: 50172 };
      resourceGot.registerUser = registerUser;

      const userCollection: IUser[] = [{ id: 48889 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [registerUser];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ resourceGot });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const resourceGot: IResourceGot = { id: 456 };
      const registerUser: IUser = { id: 61720 };
      resourceGot.registerUser = registerUser;

      activatedRoute.data = of({ resourceGot });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(registerUser);
      expect(comp.resourceGot).toEqual(resourceGot);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceGot>>();
      const resourceGot = { id: 123 };
      jest.spyOn(resourceGotFormService, 'getResourceGot').mockReturnValue(resourceGot);
      jest.spyOn(resourceGotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceGot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceGot }));
      saveSubject.complete();

      // THEN
      expect(resourceGotFormService.getResourceGot).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(resourceGotService.update).toHaveBeenCalledWith(expect.objectContaining(resourceGot));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceGot>>();
      const resourceGot = { id: 123 };
      jest.spyOn(resourceGotFormService, 'getResourceGot').mockReturnValue({ id: null });
      jest.spyOn(resourceGotService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceGot: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resourceGot }));
      saveSubject.complete();

      // THEN
      expect(resourceGotFormService.getResourceGot).toHaveBeenCalled();
      expect(resourceGotService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResourceGot>>();
      const resourceGot = { id: 123 };
      jest.spyOn(resourceGotService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resourceGot });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(resourceGotService.update).toHaveBeenCalled();
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
