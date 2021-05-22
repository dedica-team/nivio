import { IItem } from '../../../interfaces';
import { render } from '@testing-library/react';
import React from 'react';
import { getLabels, getLabelsWithPrefix } from './utils';

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
    const { queryByText } = render(<>{getLabels(item)}</>);
    expect(queryByText('java')).not.toBeInTheDocument();
    expect(queryByText('icon')).not.toBeInTheDocument();
    expect(queryByText('color')).not.toBeInTheDocument();
  });
});

describe('getLabelsWithPrefix', () => {
  it('should render prefixed labels', () => {
    const { getByText } = render(<>{getLabelsWithPrefix('framework', item)}</>);
    expect(getByText('java')).toBeInTheDocument();
  });
  it('should not render other labels', () => {
    const { queryByText } = render(<>{getLabelsWithPrefix('framework', item)}</>);
    expect(queryByText('foo')).not.toBeInTheDocument();
    expect(queryByText('icon')).not.toBeInTheDocument();
    expect(queryByText('color')).not.toBeInTheDocument();
  });
});
