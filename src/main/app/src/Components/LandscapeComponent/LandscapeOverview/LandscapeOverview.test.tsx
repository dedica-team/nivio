import React from 'react';
import { render } from '@testing-library/react';
import LandscapeOverview from './LandscapeOverview';
import { MemoryRouter } from 'react-router-dom';

jest.mock('react-console-emulator');

it('should render LandscapeOverview component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <LandscapeOverview />
    </MemoryRouter>
  );
  expect(getByText('Loading landscapes...')).toBeInTheDocument();
});
