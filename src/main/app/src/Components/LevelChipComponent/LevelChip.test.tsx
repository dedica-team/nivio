import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LevelChip from './LevelChip';

import '@testing-library/jest-dom/extend-expect'; // TODO: Why do we need it here and doesnt work from setupTests.ts ????

it('should render LevelChip component with avatar and given title', () => {
  const { getByText, queryByText } = render(
    <MemoryRouter>
      <LevelChip title={'foo'} level={'info'} />
    </MemoryRouter>
  );
  expect(getByText('foo')).toBeInTheDocument();
  expect(getByText('I')).toBeInTheDocument();
  expect(queryByText('info')).not.toBeInTheDocument();
});
