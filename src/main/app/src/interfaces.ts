export interface ILandscape {
  name: string;
  identifier: string;
  description?: string;
  teams?: object[]; // TODO: Create interface
  groups?: IGroup[];
  lastUpdate?: string;
  fullyQualifiedIdentifier?: string;
  config?: object; //TODO: CREATE INTERFACE
  labels?: ILabels;
  source?: string;
}

export interface IGroup {
  fullyQualifiedIdentifier: string;
  name?: string;
  contact?: string;
  description?: string;
  items: IItem[];
  identifier: string;
  owner?: string;
  _links?: ILinks;
  color?: string;
}

export interface IItem {
  contact?: string;
  description?: string;
  fullyQualifiedIdentifier: string;
  identifier: string;
  interfaces?: Array<Object>; //TODO: Create OBJECT Interface
  labels?: ILabels;
  name?: string;
  owner?: string;
  links?: Object;
  relations?: Array<Object>;
  tags?: Array<String>;
  type?: string;
  _links?: ILinks;
}

export interface ILabels {
  'icon'?: string;
  'nivio.rendered.color'?: string;
  'nivio.rendered.heigth'?: string;
  'nivio.rendered.icon'?: string;
  'nivio.rendered.width'?: string;
  'nivio.rendered.x'?: string;
  'nivio.rendered.y'?: string;
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

export interface IAssessment {
  results: IAssesmentResults;
  date: string;
}

export interface IAssessmentResults {
  [key: string]: IAssesmentProps[];
}

export interface IAssessmentProps {
  field: string;
  status: string;
  message: string;
}
