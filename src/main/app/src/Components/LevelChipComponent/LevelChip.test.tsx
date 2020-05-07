import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LevelChip from './LevelChip';

jest.mock('react-console-emulator');

it('should render LevelChip component with avatar and given title', () => {
  const { getByText } = render(
    <MemoryRouter>
      <LevelChip title={'foo'} level={'info'} />
    </MemoryRouter>
  );
  expect(getByText('foo')).toBeInTheDocument();
  expect(getByText('I')).toBeInTheDocument();
  //expect(getByText('info')).not.toBeInTheDocument();  TODO getByText fails
});
