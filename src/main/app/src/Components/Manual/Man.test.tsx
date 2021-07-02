import React from 'react';
import { render, RenderResult } from '@testing-library/react';
import Man from './Man';
import { MemoryRouter } from 'react-router-dom';

import * as APIClient from './../../utils/API/APIClient';
import { act } from 'react-dom/test-utils';
import { unmountComponentAtNode } from 'react-dom';

let container: HTMLElement;
beforeEach(() => {
  // setup a DOM element as a render target
  container = document.createElement('div');
  document.body.appendChild(container);
});

afterEach(() => {
  // cleanup on exiting
  unmountComponentAtNode(container);
  container.remove();
});

it('should render manual component', () => {
  const mock = jest.spyOn(APIClient, 'get'); // spy on otherFn
  mock.mockReturnValue(Promise.resolve('foo')); // mock the return value

  const { getByText } = render(
    <MemoryRouter>
      <Man setSidebarContent={() => {}} setPageTitle={() => {}} />
    </MemoryRouter>
  );
  expect(getByText("This manual page doesn't exist. :(")).toBeInTheDocument();

  mock.mockRestore();
});

it('should have the style changed to center', () => {
  const mock = jest.spyOn(APIClient, 'get'); // spy on otherFn
  mock.mockReturnValue(Promise.resolve('foo')); // mock the return value

  const { getByText } = render(
    <MemoryRouter>
      <Man setSidebarContent={() => {}} setPageTitle={() => {}} />
    </MemoryRouter>
  );
  expect(getByText("This manual page doesn't exist. :(")).toHaveStyle(`text-align: center`);
  expect(getByText("This manual page doesn't exist. :(").parentElement).not.toHaveStyle(
    `overflow-y: scroll`
  );

  mock.mockRestore();
});
