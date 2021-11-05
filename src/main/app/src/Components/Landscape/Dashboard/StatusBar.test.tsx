import React from 'react';
import { render } from '@testing-library/react';
import StatusBarLayout from './StatusBarLayout';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import landscapeContextValue from '../../../utils/testing/LandscapeContextValue';

describe('<StatusBarLayout />', () => {
  it('should display', () => {
    const { getByText } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <StatusBarLayout onItemClick={() => {}} onGroupClick={() => {}} />
      </LandscapeContext.Provider>
    );
    expect(getByText('Status')).toBeInTheDocument();
  });

  it('should display an assessment', () => {
    const { getByText } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <StatusBarLayout onItemClick={() => {}} onGroupClick={() => {}} />
      </LandscapeContext.Provider>
    );
    expect(getByText('A Group')).toBeInTheDocument();
  });
});
