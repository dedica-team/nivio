import React from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';

import LandscapeOverview from './Components/Landscape/Overview/Overview';
import LandscapeMap from './Components/Landscape/Map/Map';
import Man from './Components/Manual/Man';
import Layout from './Components/Layout/Layout';
import Events from './Components/Events/Events';
import LandscapeDashboard from './Components/Landscape/Dashboard/Dashboard';
import { Routes } from './interfaces';

import './App.scss';
import { ThemeProvider } from '@material-ui/core/styles';
import theme from './Ressources/styling/theme';

const App: React.FC = () => {
  return (
    <ThemeProvider theme={theme}>
      <Router hashType='slash'>
        <Switch>
          <Layout>
            <Route exact path='/' component={LandscapeOverview} />
            <Route exact path='/events' component={Events} />
            <Route exact path={Routes.MAP_ROUTE} component={LandscapeMap} />
            <Route exact path='/man/:usage' component={Man} />
            <Route exact path={Routes.DASHBOARD_ROUTE} component={LandscapeDashboard} />
          </Layout>
        </Switch>
      </Router>
    </ThemeProvider>
  );
};

export default App;
