import { IUser } from 'app/entities/user/user.model';
import { IResourceData } from 'app/entities/resource-data/resource-data.model';

export interface IUserProfile {
  id: number;
  user?: Pick<IUser, 'id'> | null;
  resource?: Pick<IResourceData, 'id'> | null;
}

export type NewUserProfile = Omit<IUserProfile, 'id'> & { id: null };
