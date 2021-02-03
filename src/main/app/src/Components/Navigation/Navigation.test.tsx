import React from 'react';
import { render } from '@testing-library/react';
import Navigation from './Navigation';
import { MemoryRouter } from 'react-router-dom';

it('should render Navigation component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Navigation setSidebarContent={() => {}} appBarClass={'foo'} locateFunction={() => {}} />
    </MemoryRouter>
  );
  expect(getByText('nivio')).toBeInTheDocument();
});

it('should link to manual on button click', () => {
  const { getByTestId } = render(
    <MemoryRouter>
      <Navigation setSidebarContent={() => {}} appBarClass={'foo'} locateFunction={() => {}} />
    </MemoryRouter>
  );

  expect(getByTestId('ManualButton').closest('a')).toHaveAttribute('href', '/man/install.html');
});
