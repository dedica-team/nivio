import React from 'react';
import { render } from '@testing-library/react';
import LandscapeGroup from './LandscapeGroup';

it('should render landscape group component', () => {
  const { getByText } = render(
    <LandscapeGroup fullyQualifiedGroupIdentifier={'nivio:example/test'} />
  );
  expect(getByText('Error Loading Group')).toBeInTheDocument();
});
