import { IClient, NewClient } from './client.model';

export const sampleWithRequiredData: IClient = {
  id: 71655,
};

export const sampleWithPartialData: IClient = {
  id: 33198,
};

export const sampleWithFullData: IClient = {
  id: 84776,
};

export const sampleWithNewData: NewClient = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
