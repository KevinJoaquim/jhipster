import { IUser } from 'app/entities/user/user.model';

export interface IResourceGot {
  id: number;
  gold?: number | null;
  wood?: number | null;
  fer?: number | null;
  registerUser?: Pick<IUser, 'id'> | null;
}

export type NewResourceGot = Omit<IResourceGot, 'id'> & { id: null };
