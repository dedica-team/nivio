import React from 'react';
import {BrowserRouter as Router, Switch, Route} from "react-router-dom";

import Home from "./Components/HomeComponent/Home";
import Landscape from './Components/LandscapeComponent/Landscape/Landscape';

import "./App.scss";

const App: React.FC = () => {
   /* const getMapData = (landscape: ILandscape) => {
        let params = new URLSearchParams(window.location.search);
        let data = params.get('data');
        if (data === undefined || data === null) {
            alert("data param missing");
            return;
        }

        fetch(data)
            .then((response) => {
                return response.json()
            })
            .then((json) => {
                setLandscapes(json);
                setMessage("Loaded landscapes.");
            });
    };
*/
    return (<Router>
        <Switch>
            <Route exact path="/" component={Home}></Route>
            <Route exact path="/landscape" component={Landscape}></Route>
        </Switch>
    </Router>);
};

export default App;
