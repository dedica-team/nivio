import React from 'react';
import { render } from '@testing-library/react';
import LandscapeItem from './LandscapeItem';

it('should render log component', () => {
  const { getByText } = render(
    <LandscapeItem fullyQualifiedItemIdentifier={'nivio:example/test'} />
  );
  expect(getByText('Error Loading Item')).toBeInTheDocument();
});
