import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ClientFormService } from './client-form.service';
import { ClientService } from '../service/client.service';
import { IClient } from '../client.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IResourceData } from 'app/entities/resource-data/resource-data.model';
import { ResourceDataService } from 'app/entities/resource-data/service/resource-data.service';

import { ClientUpdateComponent } from './client-update.component';

describe('Client Management Update Component', () => {
  let comp: ClientUpdateComponent;
  let fixture: ComponentFixture<ClientUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let clientFormService: ClientFormService;
  let clientService: ClientService;
  let userService: UserService;
  let resourceDataService: ResourceDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ClientUpdateComponent],
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
      .overrideTemplate(ClientUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ClientUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    clientFormService = TestBed.inject(ClientFormService);
    clientService = TestBed.inject(ClientService);
    userService = TestBed.inject(UserService);
    resourceDataService = TestBed.inject(ResourceDataService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const client: IClient = { id: 456 };
      const user: IUser = { id: 10658 };
      client.user = user;

      const userCollection: IUser[] = [{ id: 80399 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ client });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call ResourceData query and add missing value', () => {
      const client: IClient = { id: 456 };
      const company: IResourceData = { id: 18837 };
      client.company = company;

      const resourceDataCollection: IResourceData[] = [{ id: 60907 }];
      jest.spyOn(resourceDataService, 'query').mockReturnValue(of(new HttpResponse({ body: resourceDataCollection })));
      const additionalResourceData = [company];
      const expectedCollection: IResourceData[] = [...additionalResourceData, ...resourceDataCollection];
      jest.spyOn(resourceDataService, 'addResourceDataToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ client });
      comp.ngOnInit();

      expect(resourceDataService.query).toHaveBeenCalled();
      expect(resourceDataService.addResourceDataToCollectionIfMissing).toHaveBeenCalledWith(
        resourceDataCollection,
        ...additionalResourceData.map(expect.objectContaining)
      );
      expect(comp.resourceDataSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const client: IClient = { id: 456 };
      const user: IUser = { id: 38328 };
      client.user = user;
      const company: IResourceData = { id: 24128 };
      client.company = company;

      activatedRoute.data = of({ client });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.resourceDataSharedCollection).toContain(company);
      expect(comp.client).toEqual(client);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClient>>();
      const client = { id: 123 };
      jest.spyOn(clientFormService, 'getClient').mockReturnValue(client);
      jest.spyOn(clientService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ client });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: client }));
      saveSubject.complete();

      // THEN
      expect(clientFormService.getClient).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(clientService.update).toHaveBeenCalledWith(expect.objectContaining(client));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClient>>();
      const client = { id: 123 };
      jest.spyOn(clientFormService, 'getClient').mockReturnValue({ id: null });
      jest.spyOn(clientService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ client: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: client }));
      saveSubject.complete();

      // THEN
      expect(clientFormService.getClient).toHaveBeenCalled();
      expect(clientService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IClient>>();
      const client = { id: 123 };
      jest.spyOn(clientService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ client });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(clientService.update).toHaveBeenCalled();
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

    describe('compareResourceData', () => {
      it('Should forward to resourceDataService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(resourceDataService, 'compareResourceData');
        comp.compareResourceData(entity, entity2);
        expect(resourceDataService.compareResourceData).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
