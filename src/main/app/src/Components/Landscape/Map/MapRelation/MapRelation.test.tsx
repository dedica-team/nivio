import React from 'react';
import { render } from '@testing-library/react';
import MapRelation from './MapRelation';

it('should render mapRelation component', () => {
  const source = {
    identifier: 'foo',
    name: 'fooName',
    owner: '',
    contact: '',
    relations: {},
    labels: {},
    tags: [],
    icon: '',
    type: 'service',
    fullyQualifiedIdentifier: 'abc/foo',
  };
  const target = {
    identifier: 'bar',
    name: 'barName',
    owner: '',
    contact: '',
    relations: {},
    labels: {},
    tags: [],
    icon: '',
    type: 'service',
    fullyQualifiedIdentifier: 'abc/bar',
  };

  const relation =  {
    source: source.fullyQualifiedIdentifier,
    target: target.fullyQualifiedIdentifier,
    type: 'PROVIDER',
    id: target.fullyQualifiedIdentifier,
    direction: 'outbound',
    name: 'bar'
  };
  const { getByText } = render(
    <MapRelation source={source} target={target} relation={relation} locateItem={() => {}} />
  );
  expect(getByText('fooName')).toBeInTheDocument();
  expect(getByText('barName')).toBeInTheDocument();
  expect(getByText('Type: PROVIDER')).toBeInTheDocument();
});
