import { IUser } from 'app/entities/user/user.model';

export interface IResourceData {
  id: number;
  gold?: number | null;
  wood?: number | null;
  fer?: number | null;
  registerUser?: Pick<IUser, 'id'> | null;
}

export type NewResourceData = Omit<IResourceData, 'id'> & { id: null };
