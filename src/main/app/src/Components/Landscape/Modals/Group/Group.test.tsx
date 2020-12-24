import React from 'react';
import { render } from '@testing-library/react';
import Group from './Group';

it('should render landscape group component', () => {
  const group = {identifier: 'foo', fullyQualifiedIdentifier: 'bar/foo', items: []};
  const { getByText } = render(<Group group={group} assessments={null}/>);
  expect(getByText('Error Loading Group')).toBeInTheDocument();
});
