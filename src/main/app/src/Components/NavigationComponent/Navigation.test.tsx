import React from 'react';
import { render } from '@testing-library/react';
import Navigation from './Navigation';
import { MemoryRouter } from 'react-router-dom';

jest.mock('react-console-emulator');

it('should render Navigation component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Navigation />
    </MemoryRouter>
  );
  expect(getByText('Nivio')).toBeInTheDocument();
});

it('should link to manual on button click', () => {
  const { getByTestId } = render(
    <MemoryRouter>
      <Navigation />
    </MemoryRouter>
  );

  expect(getByTestId('ManualButton').closest('a')).toHaveAttribute('href', '/man/install');
});
