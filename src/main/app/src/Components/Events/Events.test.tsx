import React from 'react';
import { render } from '@testing-library/react';
import Events from './Events';
import { MemoryRouter } from 'react-router-dom';

it('should render Events component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Events />
    </MemoryRouter>
  );
  expect(getByText('Processing Event Log')).toBeInTheDocument();
});
