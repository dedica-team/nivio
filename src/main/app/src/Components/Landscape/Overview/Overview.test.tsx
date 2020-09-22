import React from 'react';
import { render } from '@testing-library/react';
import Overview from './Overview';
import { MemoryRouter } from 'react-router-dom';

it('should render LandscapeOverview component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Overview />
    </MemoryRouter>
  );
  expect(getByText('Loading landscapes...')).toBeInTheDocument();
});
