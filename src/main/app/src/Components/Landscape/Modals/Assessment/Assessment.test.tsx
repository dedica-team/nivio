import React from 'react';
import { render } from '@testing-library/react';
import Assessment from './Assessment';

it('should render assessment component', () => {
  const { getByText } = render(
    <Assessment fullyQualifiedIdentifier={'nivio:example/test'} isGroup={false} />
  );
  expect(getByText('No Assessments defined or found')).toBeInTheDocument();
});
