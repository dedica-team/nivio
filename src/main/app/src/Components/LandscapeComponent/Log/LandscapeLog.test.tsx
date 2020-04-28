import React from 'react';
import { render } from '@testing-library/react';
import LandscapeLog from './LandscapeLog';

jest.mock('react-console-emulator');

it('should render log component', () => {
  const landscape = {
    name: 'TestLandscape',
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
  };
  const { getByText } = render(<LandscapeLog landscape={landscape} />);
  expect(getByText('Landscape TestLandscape Process Log')).toBeInTheDocument();
});
