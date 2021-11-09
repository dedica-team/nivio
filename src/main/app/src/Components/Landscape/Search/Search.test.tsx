import { render } from '@testing-library/react';
import React from 'react';
import Search from './Search';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import landscapeContextValue from '../../../utils/testing/LandscapeContextValue';
import { createTheme, ThemeOptions, ThemeProvider } from '@material-ui/core/styles';
import defaultThemeVariables from '../../../Resources/styling/theme';

function MockTheme({ children }: any) {
  const tv: ThemeOptions = defaultThemeVariables;

  // @ts-ignore
  tv.palette.background.default = '#161618';
  // @ts-ignore
  tv.palette.primary.main = '#22F2C2';
  // @ts-ignore
  tv.palette.secondary.main = '#eeeeee';
  const theme = createTheme(tv);
  return <ThemeProvider theme={theme}>{children}</ThemeProvider>;
}

describe('<Search />', () => {
  it('should render', () => {
    const { getByText } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <MockTheme>
          <Search showSearch={() => {}} setSidebarContent={() => {}} />
        </MockTheme>
      </LandscapeContext.Provider>
    );
    expect(getByText('Search')).toBeInTheDocument();
  });
});
