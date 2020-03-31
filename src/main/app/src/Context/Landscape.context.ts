import React from 'react';
import { ILandscape } from '../interfaces';

interface ILandscapeContext {
  landscapes: ILandscape[];
}

const LandscapeContextDefaultValues = {
  landscapes: [
    {
      name: 'name',
      description: 'description',
      identifier: 'identifier',
      contact: 'contact',
      stats: {
        teams: ['team1', 'team2'],
        overallState: 'overallState',
        groups: ['group1', 'group2'],
        items: ['item1', 'item2'],
        lastUpdate: 'lastUpdate',
      },
    },
  ],
};

const LandscapeContext = React.createContext<ILandscapeContext>(LandscapeContextDefaultValues);

export default LandscapeContext;
