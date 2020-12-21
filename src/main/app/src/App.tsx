import React, { ReactElement, useState } from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';

import LandscapeOverview from './Components/Landscape/Overview/Overview';
import LandscapeMap from './Components/Landscape/Map/Map';
import Man from './Components/Manual/Man';
import Layout from './Components/Layout/Layout';
import LandscapeDashboard from './Components/Landscape/Dashboard/Dashboard';
import { Routes } from './interfaces';

import './App.scss';
import { ThemeProvider } from '@material-ui/core/styles';
import theme from './Ressources/styling/theme';

const App: React.FC = () => {
  const [sidebarContent, setSidebarContent] = useState<ReactElement[]>([]);
  const [pageTitle, setPageTitle] = useState<string>('');
  const [findFunction, setFindFunction] = useState<Function>(() => {});
  return (
    <ThemeProvider theme={theme}>
      <Router hashType='slash'>
        <Switch>
          <Layout
            sidebarContent={sidebarContent}
            setSidebarContent={setSidebarContent}
            findFunction={findFunction}
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
                  setFindFunction={setFindFunction}
                  setPageTitle={setPageTitle}
                  {...props}
                />
              )}
            />
            <Route exact path='/man/:usage' component={Man} />
            <Route
              exact
              path={Routes.DASHBOARD_ROUTE}
              render={(props) => (
                <LandscapeDashboard
                  setSidebarContent={setSidebarContent}
                  setFindFunction={setFindFunction}
                  setPageTitle={setPageTitle}
                  {...props}
                />
              )}
            />
          </Layout>
        </Switch>
      </Router>
    </ThemeProvider>
  );
};

export default App;
