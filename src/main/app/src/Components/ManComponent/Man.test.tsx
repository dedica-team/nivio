import React from 'react';
import { render } from '@testing-library/react';
import Man from './Man';
import { MemoryRouter } from 'react-router-dom';

jest.mock('react-console-emulator');

it('should render manual component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Man />
    </MemoryRouter>
  );
  expect(getByText('Manual')).toBeInTheDocument();
});
