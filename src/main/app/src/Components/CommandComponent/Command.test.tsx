import React from 'react';
import { render } from '@testing-library/react';
import Command from './Command';
import { MemoryRouter } from 'react-router-dom';

jest.mock('react-console-emulator');

it('should render command component', () => {
  const { getByTestId } = render(
    <MemoryRouter>
      <Command />
    </MemoryRouter>
  );
  expect(getByTestId('console')).toBeInTheDocument();
});
