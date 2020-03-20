export interface ILandscape {
    id: number,
    name?: string,
    description?: string,
    identifier?: string,
    contact?: string,
    stats: {
        teams: string[],
        overallState?: string,
        groups?: string[],
        items?: string[],
        lastUpdate?: string,
    }
}