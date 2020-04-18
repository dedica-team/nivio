import React from 'react';
import { render } from '@testing-library/react';
import Home from './Home';
import { MemoryRouter } from 'react-router-dom';

jest.mock('react-console-emulator');

it('should render command component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Home />
    </MemoryRouter>
  );
  expect(getByText('Loading landscapes...')).toBeInTheDocument();
});

it('should link to manual on button click', () => {
  const { getByTestId } = render(
    <MemoryRouter>
      <Home />
    </MemoryRouter>
  );

  expect(getByTestId('ManualButton').closest('a')).toHaveAttribute('href', '/man/install');
});
