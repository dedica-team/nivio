import React from 'react';
import { render } from '@testing-library/react';
import LandscapeLog from './LandscapeLog';

it('should render log component', () => {
  const landscape = {
    name: 'TestLandscape',
    identifier: 'identifier',
  };
  const { getByText } = render(<LandscapeLog landscape={landscape} />);
  expect(getByText('Landscape TestLandscape Process Log')).toBeInTheDocument();
});
