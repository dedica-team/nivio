import React, { ReactElement, useCallback, useEffect, useState } from 'react';
import { HashRouter as Router, Route, Switch } from 'react-router-dom';

import LandscapeOverview from './Components/Landscape/Overview/Overview';
import LandscapeMap from './Components/Landscape/Map/Map';
import Man from './Components/Manual/Man';
import Layout from './Components/Layout/Layout';
import { Routes } from './interfaces';
import defaultThemeVariables from './Ressources/styling/theme';
import { Box, CssBaseline, Theme } from '@material-ui/core';
import { createMuiTheme, ThemeOptions, ThemeProvider } from '@material-ui/core/styles';
import { get } from './utils/API/APIClient';

interface Config {
  baseUrl: string;
  version: string;
  brandingForeground: string;
  brandingBackground: string;
  brandingSecondary: string;
}

interface Index {
  config: Config;
}

const App: React.FC = () => {
  const [sidebarContent, setSidebarContent] = useState<ReactElement[]>([]);
  const [pageTitle, setPageTitle] = useState<string>('');
  const [locateFunction, setLocateFunction] = useState<Function>();
  const [theme, setTheme] = useState<Theme>();

  const ff = useCallback(
    (args) => {
      if (locateFunction) locateFunction(args);
      else console.warn('locate function not set');
    },
    [locateFunction]
  );

  useEffect(() => {
    const getColorSafely = (inputColor: string, defaultColor: string) => {
      let color: string | undefined = defaultColor;
      if (inputColor && inputColor.length > 0) {
        color = inputColor;
        if (!color.startsWith('#')) {
          color = '#' + color;
        }
      } else {
        console.log('falling back to default color', defaultColor);
      }
      return color;
    };

    get('/api/').then((value) => {
      const index: Index = value;

      const back = getColorSafely(index.config.brandingBackground, '#161618');
      const front = getColorSafely(index.config.brandingForeground, '#006868');
      const secondary = getColorSafely(index.config.brandingSecondary, '#eeeeee');

      const tv: ThemeOptions = defaultThemeVariables;
      if (!tv.palette) return;
      // @ts-ignore
      tv.palette.background.default = back;
      // @ts-ignore
      tv.palette.primary.main = front;
      // @ts-ignore
      tv.palette.secondary.main = secondary;
      setTheme(createMuiTheme(tv));
    });
  }, [setTheme]);

  if (!theme) {
    return <Box>Loading ...</Box>;
  }

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <Router hashType='slash'>
        <Switch>
          <Layout
            sidebarContent={sidebarContent}
            setSidebarContent={setSidebarContent}
            locateFunction={ff}
            pageTitle={pageTitle}
          >
            <Route
              exact
              path='/'
              render={(props) => (
                <LandscapeOverview
                  setSidebarContent={setSidebarContent}
                  setPageTitle={setPageTitle}
                  {...props}
                />
              )}
            />
            <Route
              exact
              path={Routes.MAP_ROUTE}
              render={(props) => (
                <LandscapeMap
                  setSidebarContent={setSidebarContent}
                  setLocateFunction={setLocateFunction}
                  setPageTitle={setPageTitle}
                  {...props}
                />
              )}
            />
            <Route
              exact
              path='/man/:usage'
              render={(props) => (
                <Man setSidebarContent={setSidebarContent} setPageTitle={setPageTitle} {...props} />
              )}
            />
          </Layout>
        </Switch>
      </Router>
    </ThemeProvider>
  );
};

export default App;
