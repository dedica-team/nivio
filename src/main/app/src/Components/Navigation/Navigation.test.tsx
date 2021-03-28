import React from 'react';
import { render } from '@testing-library/react';
import Navigation from './Navigation';
import { MemoryRouter } from 'react-router-dom';

it('should render Home link', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Navigation setSidebarContent={() => {}} />
    </MemoryRouter>
  );
  expect(getByText('Home')).toBeInTheDocument();
});

it('should link to manual on button click', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Navigation setSidebarContent={() => {}} />
    </MemoryRouter>
  );

  expect(getByText('Help').closest('a')).toHaveAttribute('href', '/man/install.html');
});
