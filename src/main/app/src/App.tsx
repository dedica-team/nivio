import React from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";

import Home from "./Components/HomeComponent/Home";
import Landscape from './Components/LandscapeComponent/Landscape/Landscape';

import "./App.scss";

const App: React.FC = () => {
    return (
    <Router>
        <Switch>
            <Route exact path="/" component={Home}></Route>
            <Route exact path="/landscape" component={Landscape}></Route>
        </Switch>
    </Router>);
};

export default App;
