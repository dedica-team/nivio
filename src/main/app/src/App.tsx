import React from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';

import LandscapeOverview from './Components/LandscapeComponent/LandscapeOverview/LandscapeOverview';
import LandscapeMap from './Components/LandscapeComponent/LandscapeMap/LandscapeMap';
import Man from './Components/ManComponent/Man';
import Layout from './Components/LayoutComponent/Layout';
import Events from './Components/EventComponent/Events';
import LandscapeDashboard from './Components/LandscapeComponent/LandscapeDashboard/LandscapeDashboard';
import { Routes } from './interfaces';

import './App.scss';

const App: React.FC = () => {
  return (
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
  );
};

export default App;
