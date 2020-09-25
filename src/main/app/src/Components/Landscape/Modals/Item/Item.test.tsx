import React from 'react';
import { render } from '@testing-library/react';
import Item from './Item';

it('should render landscape item component', () => {
  const { getByText } = render(<Item fullyQualifiedItemIdentifier={'nivio:example/test'} />);
  expect(getByText('Error Loading Item')).toBeInTheDocument();
});
