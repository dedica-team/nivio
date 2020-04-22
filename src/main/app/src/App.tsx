import React from 'react';
import { HashRouter as Router, Switch, Route } from 'react-router-dom';

import Home from './Components/HomeComponent/Home';
import Landscape from './Components/LandscapeComponent/Landscape/Landscape';
import Man from './Components/ManComponent/Man';

import './App.scss';

const App: React.FC = () => {
  return (
    <Router hashType='slash'>
      <Switch>
        <Route exact path='/' component={Home} />
        <Route exact path='/landscape/:identifier' component={Landscape} />
        <Route exact path='/man/:usage' component={Man} />
      </Switch>
    </Router>
  );
};

export default App;
