import React from 'react';
import { render } from '@testing-library/react';
import MapRelation from './MapRelation';
import { IRelation } from '../../../../interfaces';

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

  const relation: IRelation = {
    source: source.fullyQualifiedIdentifier,
    target: target.fullyQualifiedIdentifier,
    type: 'PROVIDER',
    direction: 'outbound',
    name: 'bar',
    labels: {},
    fullyQualifiedIdentifier:
      'relation://' + source.fullyQualifiedIdentifier + '?to=' + target.fullyQualifiedIdentifier,
    processes: {
      foo: 'process//a/foo',
    },
  };
  const { getByText, getByTestId } = render(
    <MapRelation source={source} target={target} relation={relation} setSidebarContent={() => {}} />
  );
  expect(getByText('fooName')).toBeInTheDocument();
  expect(getByText('barName')).toBeInTheDocument();
  expect(getByText('Type')).toBeInTheDocument();
  expect(getByText('PROVIDER')).toBeInTheDocument();
  expect(getByTestId('InfoIconRelation')).toBeInTheDocument();
});
