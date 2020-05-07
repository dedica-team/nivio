import React from 'react';
import { render } from '@testing-library/react';
import Landscape from './Landscape';
import { MemoryRouter } from 'react-router-dom';

jest.mock('react-console-emulator');

it('should render landscape component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Landscape />
    </MemoryRouter>
  );
  expect(getByText('No Landscapes loaded :(')).toBeInTheDocument();
});
