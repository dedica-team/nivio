export class Routes {
  static MAP_ROUTE: string = '/landscape/:identifier';
  static DASHBOARD_ROUTE: string = '/landscape/:identifier/dashboard';
}

export interface ILandscape {
  identifier: string;
  name: string;
  contact: string;
  source?: string;
  config?: IConfig;
  processLog?: IProcessLog;
  description: string;
  labels?: ILabels;
  owner: string;
  fullyQualifiedIdentifier: string;
  color?: string;
  _links?: ILinks;
  links?: ILinks;
  lastUpdate?: string;
  groups: IGroup[];
}

export interface IProcessLog {
  messages: Array<IMessages>;
  landscape?: string;
}

export interface IMessages {
  level: string;
  message: string;
  date: Date;
}

export interface IConfig {
  greedy?: boolean;
  groupLayoutConfig?: ILayoutConfig;
  itemLayoutConfig?: ILayoutConfig;
  groupBlacklist?: Array<string>;
  labelBlacklist?: Array<string>;
  branding?: IBranding;
  kpis?: IKpis;
}

export interface IKpis {
  [key: string]: IKpi;
}

export interface IKpi {
  description?: string;
  label?: string;
  messageLabel?: string;
  ranges?: IRanges;
  matches?: IMatches;
  enabled?: boolean;
}

export interface IRanges {
  [key: string]: string;
}

export interface IMatches {
  [key: string]: string;
}

export interface IBranding {
  mapStylesheet?: string;
  mapLogo?: string;
}

export interface ILayoutConfig {
  maxIterations?: number;
  forceConstantFactor?: number;
  minDistanceLimitFactor?: number;
  maxDistanceLimitFactor?: number;
}

export interface IGroup {
  owner?: string;
  color?: string;
  name?: string;
  description?: string;
  identifier: string;
  contact?: string;
  fullyQualifiedIdentifier: string;
  labels?: ILabels;
  _links?: ILinks;
  links?: ILinks;
  items: IItem[];
}

export interface IItem {
  identifier: string;
  group: string;
  name: string;
  owner: string;
  description?: string;
  contact: string;
  relations: IRelations;
  interfaces?: Array<IInterfaces>;
  labels: ILabels;
  type: string;
  fullyQualifiedIdentifier: string;
  tags: Array<string>;
  color?: string;
  icon: string;
  links?: Object;
  _links?: ILinks;
}

export interface IInterfaces {
  description?: string;
  format?: string;
  url?: string;
  protection?: string;
  deprecated?: boolean;
  name?: string;
  payload?: string;
  path?: string;
  summary?: string;
  parameters?: string;
}

export interface IRelations {
  [key: string]: IRelation;
}

export interface IRelation {
  source: string;
  target: string;
  type?: string;
  description?: string;
  format?: string;
  name: string;
  id: string;
  direction: string;
}

export interface ILabels {
  [key: string]: string;
}

export interface ILandscapeLinks {
  _links: ILinks;
  links: ILinks;
}

export interface ILinks {
  [key: string]: ILinkContent;
}

export interface ILinkContent {
  rel?: string;
  href: string;
  hreflang?: string;
  media?: string;
  title?: string;
  type?: string;
  deprecation?: string;
  name?: string;
}

export interface IAssessment {
  results: IAssessmentResults;
  date: string;
}

export interface IAssessmentResults {
  [key: string]: IAssessmentProps[];
}

export interface IAssessmentProps {
  field: string;
  status: string;
  message: string;
  maxField?: string;
  summary: boolean;
}

export interface INotificationMessage {
  timestamp: string;
  landscape: string;
  message?: string;
  level: 'success' | 'info' | 'warning' | 'error' | undefined;
  type: string;
  date: Date;
}

export interface ISnackbarMessage {
  message: string;
  key: number;
  landscape: string;
  level: "success" | "info" | "warning" | "error" | undefined;
}
