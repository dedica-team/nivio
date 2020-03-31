import React from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";

import Home from "./Components/HomeComponent/Home";
import Landscape from './Components/LandscapeComponent/Landscape/Landscape';
import Man from './Components/ManComponent/Man';

import "./App.scss";

const App: React.FC = () => {
    return (
    <Router>
        <Switch>
            <Route exact path="/" component={Home}/>
            <Route exact path="/landscape" component={Landscape}/>
            <Route exact path="/man/:usage" component={Man}/>
        </Switch>
    </Router>);
};

export default App;
