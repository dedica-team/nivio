import React from 'react';
import {HashRouter as Router, Switch, Route} from 'react-router-dom';

import LandscapeOverview from './Components/LandscapeComponent/LandscapeOverview/LandscapeOverview';
import LandscapeMap from './Components/LandscapeComponent/LandscapeMap/LandscapeMap';
import Man from './Components/ManComponent/Man';
import Layout from './Components/LayoutComponent/Layout';
import Events from './Components/EventComponent/Events';
import './App.scss';
import LandscapeDashboard from './Components/LandscapeComponent/LandscapeDashboard/LandscapeDashboard';
import {Client} from '@stomp/stompjs';

const App: React.FC = () => {

    const socketUrl = 'ws://localhost:8080/subscribe';
    const client = new Client();

    client.configure({
        brokerURL: socketUrl,
        onConnect: () => {
            client.subscribe('/topic/events', message => {
                console.log("Received processing event: ", message.body);
            });
        },
        // Helps during debugging, remove in production
        debug: (str) => {
            //console.log(new Date(), str);
        }
    });

    client.activate();

    return (
        <Router hashType='slash'>
            <Switch>
                <Layout>
                    <Route exact path='/' component={LandscapeOverview}/>
                    <Route exact path='/events' component={Events}/>
                    <Route exact path='/landscape/:identifier' component={LandscapeMap}/>
                    <Route exact path='/man/:usage' component={Man}/>
                    <Route
                        exact
                        path='/landscapeDashboard/:landscapeIdentifier'
                        component={LandscapeDashboard}
                    />
                </Layout>
            </Switch>
        </Router>
    );
};

export default App;
