import React from 'react';
import { render } from '@testing-library/react';
import Group from './Group';

it('should render landscape group component', () => {
  const group = { identifier: 'foo', name: 'foo', fullyQualifiedIdentifier: 'bar/foo', items: [] };

  const { getByText } = render(<Group group={group} />);
  expect(getByText('foo')).toBeInTheDocument();
});
