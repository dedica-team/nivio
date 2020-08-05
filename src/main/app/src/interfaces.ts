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
  name: string;
  owner: string;
  description?: string;
  contact: string;
  relations: Array<IRelations>;
  interfaces?: Array<IInterfaces>;
  labels: ILabels;
  type: string;
  fullyQualifiedIdentifier: string;
  tags: Array<String>;
  color?: string;
  icon?: string;
  links?: Object;
  _links?: ILinks;
}

export interface IInterfaces {
  description?: string;
  format?: string;
  url?: string;
  protection?: string;
}

export interface IRelations {
  source: string;
  target: string;
  type?: string;
  description?: string;
  format?: string;
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
}
