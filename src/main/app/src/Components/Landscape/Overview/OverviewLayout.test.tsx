import React from 'react';
import { render } from '@testing-library/react';
import OverviewLayout from './OverviewLayout';
import { MemoryRouter } from 'react-router-dom';
import { IGroup, IItem, ILandscape } from '../../../interfaces';

const items: IItem[] = [
  {
    contact: 'marvin',
    group: 'test',
    owner: 'daniel',
    fullyQualifiedIdentifier: 'fullTestIdentifier',
    identifier: 'testIdentifier',
    name: 'testIdentifier',
    description: 'testDescription',
    relations: {},
    labels: {},
    tags: [],
    type: 'service',
    icon: '',
    networks: ['lan'],
  },
];

const groups: IGroup[] = [
  {
    fullyQualifiedIdentifier: 'groupIdentifier',
    name: 'groupName',
    items: items,
    identifier: 'groupIdentifier',
    icon: '',
  },
];

const landscapes: ILandscape = {
  name: 'landscapeTestName',
  identifier: 'testIdentifier',
  description: 'testIdentifier',
  groups: groups,
  contact: 'marvin',
  owner: 'daniel',
  fullyQualifiedIdentifier: 'fullTestIdentifier',
};
it('should render LandscapeOverview component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <OverviewLayout landscapes={[landscapes]} />
    </MemoryRouter>
  );
  expect(getByText('landscapeTestName')).toBeInTheDocument();
});
