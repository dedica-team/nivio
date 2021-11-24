import React from 'react';
import { fireEvent, getByTitle, queryByText, render, waitFor } from '@testing-library/react';
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
    labels: { 'framework.spring boot': '2.2', 'team': 'ops guys' },
    type: 'foo',
    fullyQualifiedIdentifier: 'foo',
    tags: [],
    color: 'foo',
    icon: 'foo',
    _links: { homepage: { href: 'http://acme.com' } },
    networks: ['vpn'],
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

  it('should display networks, frameworks and other labels', async () => {
    //given
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(useItem));

    //when
    const { container, queryByText } = render(<Item fullyQualifiedItemIdentifier={'foo'} />);
    fireEvent.click(getByTitle(container, 'API / Interfaces'));

    //then
    await waitFor(() => expect(mock).toHaveBeenCalledTimes(1));
    await waitFor(() => expect(queryByText('vpn')).toBeInTheDocument());
    await waitFor(() => expect(queryByText('Networks')).toBeInTheDocument());
    await waitFor(() => expect(queryByText('spring boot')).toBeInTheDocument());
    await waitFor(() => expect(queryByText('ops guys')).toBeInTheDocument());
  });

  it('check if mui info icon appears', async() => {
    // given
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(useItem));

    //when
    const { container, queryByTestId } = render(<Item fullyQualifiedItemIdentifier={'foo'} />);
    fireEvent.click(getByTitle(container, 'Relations'));


    // then
    await waitFor(() => expect(queryByTestId('testInfoIcon')).toBeInTheDocument());
  });


});
