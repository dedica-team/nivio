import { fireEvent, getByTitle, render, waitFor } from '@testing-library/react';
import React from 'react';
import Search from './Search';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import landscapeContextValue from '../../../utils/testing/LandscapeContextValue';
import { createTheme, ThemeOptions, ThemeProvider } from '@material-ui/core/styles';
import defaultThemeVariables from '../../../Resources/styling/theme';
import { IFacet } from '../../../interfaces';
import * as APIClient from '../../../utils/API/APIClient';

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

const facets: IFacet[] = [
  {
    dim: 'kpi_security',
    value: 19,
    labelValues: [
      {
        label: 'green',
        value: 12,
        color: '#161618',
      },
      {
        label: 'red',
        value: 7,
        color: '#161618',
      },
    ],
  },
  {
    dim: 'owner',
    value: 12,
    labelValues: [
      {
        label: 'foo',
        value: 5,
        color: '',
      },
      {
        label: 'bar',
        value: 7,
        color: '#161618',
      },
    ],
  },
];
describe('<Search />', () => {
  it('should render', () => {
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(facets));

    const { getByText } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <Search searchTerm={'foo'} setSearchTerm={() => {}} />
      </LandscapeContext.Provider>
    );
    expect(getByText('Search')).toBeInTheDocument();
  });
  it('should add facet to search term', async () => {
    const mock = jest.spyOn(APIClient, 'get');
    mock.mockReturnValue(Promise.resolve(facets));
    const { container, getByText } = render(
      <LandscapeContext.Provider value={landscapeContextValue}>
        <MockTheme>
          <Search searchTerm={''} setSearchTerm={() => {}} />
        </MockTheme>
      </LandscapeContext.Provider>
    );

    fireEvent.click(getByTitle(container, 'Fields'));

    await waitFor(() => getByText('foo'));
    const fooFacetChip = getByText('foo');
    expect(fooFacetChip).toBeInTheDocument();
  });
});
