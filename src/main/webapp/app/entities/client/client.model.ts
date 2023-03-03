import { IUser } from 'app/entities/user/user.model';
import { IResourceData } from 'app/entities/resource-data/resource-data.model';

export interface IClient {
  id: number;
  user?: Pick<IUser, 'id'> | null;
  company?: Pick<IResourceData, 'id'> | null;
}

export type NewClient = Omit<IClient, 'id'> & { id: null };
