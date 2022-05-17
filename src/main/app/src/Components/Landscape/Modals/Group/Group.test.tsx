import React from 'react';
import { render } from '@testing-library/react';
import Group from './Group';

it('should render landscape group component', () => {
  const group = {
    identifier: 'foo',
    name: 'foo',
    fullyQualifiedIdentifier: 'bar/foo',
    items: [],
    icon: 'foo.png',
  };

  const { getByText } = render(<Group defaultGroup={group} />);
  expect(getByText('foo')).toBeInTheDocument();
});
