import React from 'react';
import { ILandscape } from '../interfaces';

/**
 * LandscapeContext is used to save all existing landscapes
 * This way landscapes dont have to be fetched always when opening a new side
 */
interface ILandscapeContext {
  landscapes: ILandscape[];
}

const LandscapeContextDefaultValues = {
  landscapes: [
    {
      name: 'name',
      identifier: 'identifier',
    },
  ],
};

const LandscapeContext = React.createContext<ILandscapeContext>(LandscapeContextDefaultValues);

export default LandscapeContext;
