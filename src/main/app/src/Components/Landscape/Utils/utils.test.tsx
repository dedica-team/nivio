import { IItem } from '../../../interfaces';
import { render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import OverviewLayout from '../Overview/OverviewLayout';
import React from 'react';
import { getLabels } from './utils';

const item: IItem = {
  contact: 'marvin',
  group: 'test',
  owner: 'daniel',
  fullyQualifiedIdentifier: 'fullTestIdentifier',
  identifier: 'testIdentifier',
  name: 'testIdentifier',
  description: 'testDescription',
  relations: {},
  labels: {
    'foo': 'bar',
    'color': 'ffeecc',
    'icon': 'hiu',
    'framework.java': '8',
    'framework.react': '84711',
  },
  tags: [],
  type: 'service',
  icon: '',
};

describe('getLabels', () => {
  it('should render labels', () => {
    const { getByText } = render(<>{getLabels(item)}</>);
    expect(getByText('foo')).toBeInTheDocument();
  });
  it('should not render hidden labels', () => {
    const { getByText, queryByText } = render(<>{getLabels(item)}</>);
    expect(queryByText('java')).not.toBeInTheDocument();
    expect(queryByText('icon')).not.toBeInTheDocument();
    expect(queryByText('color')).not.toBeInTheDocument();
  });
});
