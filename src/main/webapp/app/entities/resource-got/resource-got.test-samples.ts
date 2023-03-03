import { IResourceGot, NewResourceGot } from './resource-got.model';

export const sampleWithRequiredData: IResourceGot = {
  id: 37195,
};

export const sampleWithPartialData: IResourceGot = {
  id: 75026,
};

export const sampleWithFullData: IResourceGot = {
  id: 36345,
  gold: 42569,
  wood: 98130,
  fer: 45020,
};

export const sampleWithNewData: NewResourceGot = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
