import React from 'react';
import { render } from '@testing-library/react';
import MapRelation from './MapRelation';

it('should render mapRelation component', () => {
  const { getByText } = render(
    <MapRelation
      sourceIdentifier={'source'}
      targetIdentifier={'target'}
      type='typeTest'
      findItem={() => {}}
    />
  );
  expect(getByText('source')).toBeInTheDocument();
  expect(getByText('target')).toBeInTheDocument();
  expect(getByText('Type: typeTest')).toBeInTheDocument();
});
