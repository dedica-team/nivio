export interface ILandscape {
  name: string;
  identifier: string;
  description?: string;
  teams?: object[];
  groups?: object[];
  items?: string[];
  lastUpdate?: string;
  overallState?: string;
}
