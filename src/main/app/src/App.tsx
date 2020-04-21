import React from 'react';
import { BrowserRouter as Router, Switch, Route } from 'react-router-dom';

import LandscapeOverview from './Components/LandscapeComponent/LandscapeOverview/LandscapeOverview';
import Landscape from './Components/LandscapeComponent/Landscape/Landscape';
import Man from './Components/ManComponent/Man';
import Layout from './Components/LayoutComponent/Layout';

import './App.scss';

const App: React.FC = () => {
  return (
    <Router>
      <Switch>
        <Layout>
          <Route exact path='/' component={LandscapeOverview} />
          <Route exact path='/landscape/:identifier' component={Landscape} />
          <Route exact path='/man/:usage' component={Man} />
        </Layout>
      </Switch>
    </Router>
  );
};

export default App;
