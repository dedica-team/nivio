import React from 'react';
import { render } from '@testing-library/react';
import LandscapeAssessment from './LandscapeAssessment';

it('should render assessment component', () => {
  const { getByText } = render(
    <LandscapeAssessment fullyQualifiedIdentifier={'nivio:example/test'} isGroup={false} />
  );
  expect(getByText('No Assessments defined or found')).toBeInTheDocument();
});
