import React, { ReactElement, useEffect, useState } from 'react';
import { HashRouter as Router, Route, Switch } from 'react-router-dom';

import LandscapeOverview from './Components/Landscape/Overview/Overview';
import LandscapeMap from './Components/Landscape/Map/Map';
import Man from './Components/Manual/Man';
import Layout from './Components/Layout/Layout';
import { Routes } from './interfaces';
import { Box, CssBaseline, Theme } from '@material-ui/core';
import { createTheme, ThemeOptions, ThemeProvider } from '@material-ui/core/styles';
import { get } from './utils/API/APIClient';
import defaultThemeVariables from './Resources/styling/theme';

interface Config {
  baseUrl: string;
  version: string;
  brandingForeground: string;
  brandingBackground: string;
  brandingSecondary: string;
  brandingLogoUrl: string;
  brandingMessage: string;
}

interface Index {
  config: Config;
}

const App: React.FC = () => {
  const [sidebarContent, setSidebarContent] = useState<ReactElement[]>([]);
  const [pageTitle, setPageTitle] = useState<string>('');
  const [logo, setLogo] = useState<string>('');
  const [message, setMessage] = useState<string>('');
  const [version, setVersion] = useState<string>();
  const [theme, setTheme] = useState<Theme>();

  useEffect(() => {
    const getColorSafely = (inputColor: string, defaultColor: string) => {
      let color: string | undefined = defaultColor;
      if (inputColor && inputColor.length > 0) {
        color = inputColor;
        if (!color.startsWith('#')) {
          color = '#' + color;
        }
      } else {
        console.debug('falling back to default color', defaultColor);
      }
      return color;
    };

    get('/api/').then((value) => {
      const index: Index = value;

      const back = getColorSafely(index.config.brandingBackground, '#161618');
      const front = getColorSafely(index.config.brandingForeground, '#22F2C2');
      const secondary = getColorSafely(index.config.brandingSecondary, '#eeeeee');
      setMessage(index.config.brandingMessage);
      setVersion(index.config.version);

      const tv: ThemeOptions = defaultThemeVariables;
      if (!tv.palette) return;
      // @ts-ignore
      tv.palette.background.default = back;
      // @ts-ignore
      tv.palette.primary.main = front;
      // @ts-ignore
      tv.palette.secondary.main = secondary;

      if (tv.typography) {
        // @ts-ignore
        tv.typography.h3.color = secondary;
        // @ts-ignore
        tv.typography.h4.color = secondary;
        // @ts-ignore
        tv.typography.h5.color = secondary;
        // @ts-ignore
        tv.typography.h6.color = secondary;
      }

      if (index.config.brandingLogoUrl && index.config.brandingLogoUrl.length) {
        setLogo(index.config.brandingLogoUrl);
      }
      setTheme(createTheme(tv));
    });
  }, [setTheme, setLogo]);

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
            logo={logo}
            pageTitle={pageTitle}
            version={version}
          >
            <Route
              exact
              path='/'
              render={(props) => (
                <LandscapeOverview
                  setSidebarContent={setSidebarContent}
                  setPageTitle={setPageTitle}
                  welcomeMessage={message}
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
