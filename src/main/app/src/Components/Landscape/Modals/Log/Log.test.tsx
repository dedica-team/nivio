import React from 'react';
import { render } from '@testing-library/react';
import Log from './Log';

it('should render log component', () => {
  const landscape = {
    name: 'TestLandscape',
    identifier: 'identifier',
  };
  const { getByText } = render(<Log landscape={landscape} />);
  expect(getByText('Process Log of TestLandscape')).toBeInTheDocument();
});
