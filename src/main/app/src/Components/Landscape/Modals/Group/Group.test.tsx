import React from 'react';
import { render } from '@testing-library/react';
import Group from './Group';

it('should render landscape group component', () => {
  const { getByText } = render(<Group fullyQualifiedGroupIdentifier={'nivio:example/test'} />);
  expect(getByText('Error Loading Group')).toBeInTheDocument();
});
