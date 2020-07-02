export interface ILandscape {
  name: string;
  identifier: string;
  description?: string;
  teams?: object[]; // TODO: Create interface
  items?: IItem[];
  groups?: IGroup[];
  lastUpdate?: string;
}

export interface IGroup {
  fullyQualifiedIdentifier: string;
  name: string;
  contact?: string;
  description?: string;
  items: IItem[];
}

export interface IItem {
  color?: string;
  contact?: string;
  description?: string;
  fill?: string;
  fullyQualifiedIdentifier: string;
  group?: string;
  height?: number;
  icon?: string;
  identifier: string;
  interfaces?: Array<Object>;
  labels?: Object;
  lifecycle?: String;
  links?: Object;
  name: String;
  owner?: String;
  providedBy?: Array<Object>;
  relations?: Array<Object>;
  tags?: Array<Object>;
  type?: String;
  width?: number;
  x?: number;
  y?: number;
}

export interface ILandscapeLinks {
  _links: ILinks;
}

export interface ILinks {
  [key: string]: ILinkContent;
}

export interface ILinkContent {
  rel: string;
  href: string;
  media: string;
  name: string;
}
