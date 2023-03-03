import { IResource, NewResource } from './resource.model';

export const sampleWithRequiredData: IResource = {
  id: 29528,
};

export const sampleWithPartialData: IResource = {
  id: 34047,
  wood: 40133,
};

export const sampleWithFullData: IResource = {
  id: 14820,
  gold: 68444,
  wood: 96719,
  fer: 60754,
};

export const sampleWithNewData: NewResource = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
