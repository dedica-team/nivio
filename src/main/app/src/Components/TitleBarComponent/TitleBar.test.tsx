import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import TitleBar from "./TitleBar";

jest.mock('react-console-emulator');

it('should render LevelChip component with given title', () => {
  const { getByText } = render(
    <MemoryRouter>
      <TitleBar title={'foo'} />
    </MemoryRouter>
  );
  expect(getByText('foo')).toBeInTheDocument();
});
