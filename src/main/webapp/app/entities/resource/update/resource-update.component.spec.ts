import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ResourceFormService } from './resource-form.service';
import { ResourceService } from '../service/resource.service';
import { IResource } from '../resource.model';
import { IClient } from 'app/entities/client/client.model';
import { ClientService } from 'app/entities/client/service/client.service';

import { ResourceUpdateComponent } from './resource-update.component';

describe('Resource Management Update Component', () => {
  let comp: ResourceUpdateComponent;
  let fixture: ComponentFixture<ResourceUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let resourceFormService: ResourceFormService;
  let resourceService: ResourceService;
  let clientService: ClientService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ResourceUpdateComponent],
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
      .overrideTemplate(ResourceUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ResourceUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    resourceFormService = TestBed.inject(ResourceFormService);
    resourceService = TestBed.inject(ResourceService);
    clientService = TestBed.inject(ClientService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Client query and add missing value', () => {
      const resource: IResource = { id: 456 };
      const client: IClient = { id: 93140 };
      resource.client = client;

      const clientCollection: IClient[] = [{ id: 34332 }];
      jest.spyOn(clientService, 'query').mockReturnValue(of(new HttpResponse({ body: clientCollection })));
      const additionalClients = [client];
      const expectedCollection: IClient[] = [...additionalClients, ...clientCollection];
      jest.spyOn(clientService, 'addClientToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ resource });
      comp.ngOnInit();

      expect(clientService.query).toHaveBeenCalled();
      expect(clientService.addClientToCollectionIfMissing).toHaveBeenCalledWith(
        clientCollection,
        ...additionalClients.map(expect.objectContaining)
      );
      expect(comp.clientsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const resource: IResource = { id: 456 };
      const client: IClient = { id: 93689 };
      resource.client = client;

      activatedRoute.data = of({ resource });
      comp.ngOnInit();

      expect(comp.clientsSharedCollection).toContain(client);
      expect(comp.resource).toEqual(resource);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResource>>();
      const resource = { id: 123 };
      jest.spyOn(resourceFormService, 'getResource').mockReturnValue(resource);
      jest.spyOn(resourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resource }));
      saveSubject.complete();

      // THEN
      expect(resourceFormService.getResource).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(resourceService.update).toHaveBeenCalledWith(expect.objectContaining(resource));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResource>>();
      const resource = { id: 123 };
      jest.spyOn(resourceFormService, 'getResource').mockReturnValue({ id: null });
      jest.spyOn(resourceService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resource: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: resource }));
      saveSubject.complete();

      // THEN
      expect(resourceFormService.getResource).toHaveBeenCalled();
      expect(resourceService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IResource>>();
      const resource = { id: 123 };
      jest.spyOn(resourceService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ resource });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(resourceService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareClient', () => {
      it('Should forward to clientService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(clientService, 'compareClient');
        comp.compareClient(entity, entity2);
        expect(clientService.compareClient).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
