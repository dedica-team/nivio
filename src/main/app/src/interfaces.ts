export class Routes {
  static MAP_ROUTE: string = '/landscape/:identifier';
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
  kpis?: IKpis;
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
}

export interface IKpis {
  [key: string]: IKpi;
}

export interface IKpi {
  description?: string;
  label?: string;
  ranges?: IRanges;
  matches?: IMatches;
  enabled?: boolean;
}

export interface IRanges {
  [key: string]: IRange;
}

export interface IRange {
  minimum: string;
  maximum: string;
  description?: string;
}

export interface IMatches {
  [key: string]: string[];
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
  address?: string;
  contact: string;
  relations: IRelations;
  interfaces?: Array<IInterfaces>;
  labels: ILabels;
  type: string;
  fullyQualifiedIdentifier: string;
  tags: Array<string>;
  color?: string;
  icon: string;
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
  labels: ILabels;
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

export interface IChange {
  changeType: string;
  componentType: string;
  message: string;
}

/**
 * Map of changes. Key is the FQI. A relation has two FQIs separated by semicolon.
 */
export interface IChanges {
  [key: string]: IChange;
}

export interface INotificationMessage {
  timestamp: string;
  landscape: string;
  message?: string;
  level: 'success' | 'info' | 'warning' | 'error' | undefined;
  type: string;
  date: Date;
  changelog: { changes: IChanges };
}

export interface IFacet {
  /**
   * label / title
   */
  dim: string;
  //path: [];
  /**
   * total count
   */
  value: number;
  /**
   * different label counts
   */
  labelValues: ILabelValue[];
}

export interface ILabelValue {
  label: string;
  value: number;
}
