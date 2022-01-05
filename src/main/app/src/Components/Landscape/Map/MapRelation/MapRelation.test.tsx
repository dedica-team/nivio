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
    group: 'Customers',
    networks: ['lan'],
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
    group: 'Billing',
    networks: ['vpn', 'lan'],
  };

  const relation = {
    source: source.fullyQualifiedIdentifier,
    target: target.fullyQualifiedIdentifier,
    type: 'PROVIDER',
    id: target.fullyQualifiedIdentifier,
    direction: 'outbound',
    name: 'bar',
    labels: {},
  };
  const { getByText, getByTestId } = render(
    <MapRelation source={source} target={target} relation={relation} />
  );
  expect(getByText('fooName')).toBeInTheDocument();
  expect(getByText('barName')).toBeInTheDocument();
  expect(getByText('Type')).toBeInTheDocument();
  expect(getByText('PROVIDER')).toBeInTheDocument();
  expect(getByTestId('InfoIconRelation')).toBeInTheDocument();
});
