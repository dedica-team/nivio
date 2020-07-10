import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LandscapeDashboard from './LandscapeDashboard';
import LandscapeDashboardLayout from './LandscapeDashboardLayout';
import { ILandscape, IItem, IGroup } from '../../../interfaces';

it('should render LandscapeDashboard component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <LandscapeDashboard />
    </MemoryRouter>
  );
  expect(getByText('Loading landscapes...')).toBeInTheDocument();
});

it('should display all group information', () => {
  const items: IItem[] = [
    {
      contact: 'marvin',
      owner: 'daniel',
      fullyQualifiedIdentifier: 'fullTestIdentifier',
      identifier: 'testIdentifier',
      description: 'testDescription',
    },
  ];

  const groups: IGroup[] = [
    {
      fullyQualifiedIdentifier: 'groupIdentifier',
      name: 'groupName',
      items,
      identifier: 'groupIdentifier',
    },
  ];

  const landscape: ILandscape = {
    name: 'landscapeTestName',
    identifier: 'testIdentifier',
    groups,
    lastUpdate: 'gestern',
  };
  const { getByText } = render(
    <MemoryRouter>
      <LandscapeDashboardLayout landscape={landscape} assesments={null} onItemClick={() => {}} />
    </MemoryRouter>
  );
  expect(getByText('landscapeTestName')).toBeInTheDocument();
  expect(getByText('groupName')).toBeInTheDocument();
  expect(getByText('testIdentifier')).toBeInTheDocument();
});
