export interface ILandscape {
  name: string;
  identifier: string;
  description?: string;
  contact?: string;
  stats: {
    teams: string[];
    overallState?: string;
    groups?: string[];
    items?: string[];
    lastUpdate?: string;
  };
}
