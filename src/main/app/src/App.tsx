import React from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';

import LandscapeOverview from './Components/LandscapeComponent/LandscapeOverview/LandscapeOverview';
import Landscape from './Components/LandscapeComponent/LandscapeMap/Landscape';
import Man from './Components/ManComponent/Man';
import Layout from './Components/LayoutComponent/Layout';
import Events from './Components/EventComponent/Events';
import './App.scss';

const App: React.FC = () => {
  return (
    <Router hashType='slash'>
      <Switch>
        <Layout>
          <Route exact path='/' component={LandscapeOverview} />
          <Route exact path='/events' component={Events} />
          <Route exact path='/landscape/:identifier' component={Landscape} />
          <Route exact path='/man/:usage' component={Man} />
        </Layout>
      </Switch>
    </Router>
  );
};

export default App;
