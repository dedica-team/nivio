import { getByAltText, render } from '@testing-library/react';
import React from 'react';
import Layout from './Layout';
import { MemoryRouter } from 'react-router-dom';

describe('<Layout />', () => {
  it('should render navigation with title, logo and version', () => {
    const { getByText, getByAltText } = render(
      <MemoryRouter>
        <Layout
          pageTitle={'foo'}
          logo={'https://acme.com/logo.png'}
          children={<div></div>}
          version={'123'}
        />
      </MemoryRouter>
    );
    expect(getByAltText('logo')).toHaveAttribute('src', 'https://acme.com/logo.png');
    expect(getByText('foo')).toBeInTheDocument();
    expect(getByText('nivio 123')).toBeInTheDocument();
  });
});
