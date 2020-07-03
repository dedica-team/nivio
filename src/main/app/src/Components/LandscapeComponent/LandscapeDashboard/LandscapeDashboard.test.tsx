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
    name: 'Test',
    identifier: 'testIdentifier',
    groups,
    lastUpdate: 'gestern',
  };
  const { getByText } = render(
    <MemoryRouter>
      <LandscapeDashboardLayout landscape={landscape} />
    </MemoryRouter>
  );
  expect(getByText('Landscape: Test')).toBeInTheDocument();
  expect(getByText('groupName')).toBeInTheDocument();
  expect(getByText('Name')).toBeInTheDocument();
  expect(getByText('testIdentifier')).toBeInTheDocument();
  expect(getByText('testDescription')).toBeInTheDocument();
  expect(getByText('Contact')).toBeInTheDocument();
  expect(getByText('marvin')).toBeInTheDocument();
  expect(getByText('Owner')).toBeInTheDocument();
  expect(getByText('daniel')).toBeInTheDocument();
});
