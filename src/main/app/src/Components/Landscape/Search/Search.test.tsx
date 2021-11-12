import { render } from '@testing-library/react';
import React from 'react';
import Search from './Search';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import landscapeContextValue from '../../../utils/testing/LandscapeContextValue';

describe('<Search />', () => {
  it('should render', () => {
    const { getByText } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <Search searchTerm={'foo'} setSearchTerm={() => {}}/>
      </LandscapeContext.Provider>
    );
    expect(getByText('Search')).toBeInTheDocument();
  });
});
