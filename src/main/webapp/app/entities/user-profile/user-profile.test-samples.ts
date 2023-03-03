import { IUserProfile, NewUserProfile } from './user-profile.model';

export const sampleWithRequiredData: IUserProfile = {
  id: 10373,
};

export const sampleWithPartialData: IUserProfile = {
  id: 55443,
};

export const sampleWithFullData: IUserProfile = {
  id: 60119,
};

export const sampleWithNewData: NewUserProfile = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
