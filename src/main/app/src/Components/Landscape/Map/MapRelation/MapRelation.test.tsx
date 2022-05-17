import React from 'react';
import { act, render } from '@testing-library/react';
import MapRelation from './MapRelation';

it('should render mapRelation component', () => {
  const source = {
    identifier: 'foo',
    name: 'fooName',
    owner: '',
    contact: '',
    labels: {},
    tags: [],
    icon: '',
    type: 'service',
    fullyQualifiedIdentifier: 'abc/foo',
    group: 'Customers',
    networks: ['lan'],
    relations: {
      'abc/foo;abc/bar': {
        source: 'abc/foo',
        target: 'abc/bar',
        type: 'PROVIDER',
        id: 'abc/bar',
        direction: 'outbound',
        name: 'bar',
        labels: {},
      },
    },
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

  act(() => {
    const { getByText, getByTestId } = render(
      <MapRelation
        defaultSource={source}
        target={target}
      />
    );

    expect(getByText('fooName')).toBeInTheDocument();
    expect(getByText('barName')).toBeInTheDocument();
    expect(getByText('Type')).toBeInTheDocument();
    expect(getByText('PROVIDER')).toBeInTheDocument();
    expect(getByTestId('InfoIconRelation')).toBeInTheDocument();
  });
});
