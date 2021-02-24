import React from 'react';
import { render } from '@testing-library/react';
import Man from './Man';
import { MemoryRouter } from 'react-router-dom';

it('should render manual component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Man setSidebarContent={() => { }} setPageTitle={() => { }} />
    </MemoryRouter>
  );
  expect(getByText("This manual page doesn't exist. :(")).toBeInTheDocument();

});

it('should have the style changed to center', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Man setSidebarContent={() => { }} setPageTitle={() => { }} />
    </MemoryRouter>
  );
  expect(getByText("This manual page doesn't exist. :(")).toHaveStyle(`text-align: center`);
  expect(getByText("This manual page doesn't exist. :(").parentElement).not.toHaveStyle(`overflow-y: scroll`);

});