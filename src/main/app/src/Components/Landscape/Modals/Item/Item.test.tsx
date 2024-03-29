import React from 'react';
import { fireEvent, getByTitle, queryByText, render, waitFor } from '@testing-library/react';
import * as APIClient from '../../../../utils/API/APIClient';
import Item from './Item';
import { IItem } from '../../../../interfaces';
import { LandscapeContext } from '../../../../Context/LandscapeContext';
import landscapeContextValue from '../../../../utils/testing/LandscapeContextValue';

describe('<Item />', () => {
  const IRelations = {
    source: 'foo',
    target: 'test/groupA/foo',
    description: 'foo',
    format: 'foo',
    name: 'web',
    id: 'foo',
    direction: 'outbound',
    labels: {},
    type: 'PROVIDER',
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
    const { getByText } = render(<Item fullyQualifiedItemIdentifier={'foo'} />);

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

  it('check if mui info icon appears', async () => {
    // given
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(useItem));

    //when
    const { container, getByTestId } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <Item fullyQualifiedItemIdentifier={'foo'} />
      </LandscapeContext.Provider>
    );

    fireEvent.click(getByTitle(container, 'Relations'));

    // then
    await waitFor(() => expect(getByTestId('InfoIcon')));
  });
});
