import React from 'react';
import { render } from '@testing-library/react';
import MapRelation from './MapRelation';

it('should render mapRelation component', () => {
  const { getByText } = render(
    <MapRelation
      sourceIdentifier={'source/test/source'}
      targetIdentifier={'target/test/target'}
      type='typeTest'
      locateItem={() => {}}
    />
  );
  expect(getByText('test/source')).toBeInTheDocument();
  expect(getByText('test/target')).toBeInTheDocument();
  expect(getByText('Type: typeTest')).toBeInTheDocument();
});
