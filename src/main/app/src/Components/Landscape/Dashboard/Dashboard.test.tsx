import React from 'react';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from './Dashboard';
import DashboardLayout from './DashboardLayout';
import { ILandscape, IItem, IGroup } from '../../../interfaces';

it('should render LandscapeDashboard component', () => {
  const { getByText } = render(
    <MemoryRouter>
      <Dashboard setSidebarContent={() =>{}} setPageTitle={() =>{}} setFindFunction={() =>{}}/>
    </MemoryRouter>
  );
});

it('should display all group information', () => {
  const items: IItem[] = [
    {
      contact: 'marvin',
      owner: 'daniel',
      fullyQualifiedIdentifier: 'fullTestIdentifier',
      identifier: 'testIdentifier',
      name: 'testIdentifier',
      description: 'testDescription',
      relations: [],
      labels: {},
      tags: [],
      type: 'service',
      icon: ''
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
    description: 'testIdentifier',
    groups,
    lastUpdate: 'gestern',
    contact: 'marvin',
    owner: 'daniel',
    fullyQualifiedIdentifier: 'fullTestIdentifier',
  };
  const { getByText } = render(
    <MemoryRouter>
      <DashboardLayout
        landscape={landscape}
        assessments={undefined}
        onItemClick={() => {}}
        onItemAssessmentClick={() => {}}
        onGroupClick={() => {}}
        onGroupAssessmentClick={() => {}}
      />
    </MemoryRouter>
  );
  expect(getByText('groupName')).toBeInTheDocument();
});
