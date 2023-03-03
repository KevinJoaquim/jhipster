import { IClient } from 'app/entities/client/client.model';

export interface IResource {
  id: number;
  gold?: number | null;
  wood?: number | null;
  fer?: number | null;
  client?: Pick<IClient, 'id'> | null;
}

export type NewResource = Omit<IResource, 'id'> & { id: null };
