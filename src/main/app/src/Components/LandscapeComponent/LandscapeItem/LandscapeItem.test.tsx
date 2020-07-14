import React from 'react';
import { render } from '@testing-library/react';
import LandscapeItem from './LandscapeItem';

it('should render log component', () => {
  const element = document.createElement('div');
  const { getByText } = render(
    <LandscapeItem fullyQualifiedItemIdentifier={'nivio:example/test'} />
  );
  expect(getByText('Contact:')).toBeInTheDocument();
});
