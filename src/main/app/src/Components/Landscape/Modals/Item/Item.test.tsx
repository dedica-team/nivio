import React from 'react';
import { render, waitFor } from '@testing-library/react';
import * as APIClient from '../../../../utils/API/APIClient';
import Item from './Item';
import { IItem } from '../../../../interfaces';

describe('<Item />', () => {
  const IRelations = {
    source: 'foo',
    target: 'foo',
    description: 'foo',
    format: 'foo',
    name: 'foo',
    id: 'foo',
    direction: 'foo',
    labels: {},
  };
  const Irelations = { foo: IRelations };
  const useItem: IItem = {
    identifier: 'foo',
    group: 'foo',
    name: 'foo',
    owner: 'foo',
    description: 'foo',
    contact: 'foo',
    relations: Irelations,
    interfaces: [],
    labels: { foo: 'foo' },
    type: 'foo',
    fullyQualifiedIdentifier: 'foo',
    tags: [],
    color: 'foo',
    icon: 'foo',
    _links: { homepage: { href: 'http://acme.com' } },
  };

  it('should avoid displaying undefined and null value', () => {
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(useItem));

    const { queryByText } = render(<Item fullyQualifiedItemIdentifier={'foo'} />);

    expect(queryByText('foo (undefined foo), format: foo')).toBeNull();
    expect(queryByText('undefined')).toBeNull();
    expect(queryByText('null')).toBeNull();
  });

  it('should display links', async () => {
    //given
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(useItem));

    //when
    const { container, getByText } = render(<Item fullyQualifiedItemIdentifier={'foo'} />);

    //then
    await waitFor(() => expect(mock).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(getByText('homepage')).toBeInTheDocument());
  });
});
