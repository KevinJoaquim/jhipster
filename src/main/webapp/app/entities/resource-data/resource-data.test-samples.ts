import { IResourceData, NewResourceData } from './resource-data.model';

export const sampleWithRequiredData: IResourceData = {
  id: 82397,
};

export const sampleWithPartialData: IResourceData = {
  id: 87235,
};

export const sampleWithFullData: IResourceData = {
  id: 89652,
  gold: 29527,
  wood: 78972,
  fer: 16811,
};

export const sampleWithNewData: NewResourceData = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
