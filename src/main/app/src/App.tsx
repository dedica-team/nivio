import React, { ReactElement, useCallback, useState } from 'react';
import { HashRouter as Router, Route, Switch } from 'react-router-dom';

import LandscapeOverview from './Components/Landscape/Overview/Overview';
import LandscapeMap from './Components/Landscape/Map/Map';
import Man from './Components/Manual/Man';
import Layout from './Components/Layout/Layout';
import { Routes } from './interfaces';

const App: React.FC = () => {
  const [sidebarContent, setSidebarContent] = useState<ReactElement[]>([]);
  const [pageTitle, setPageTitle] = useState<string>('');
  const [findFunction, setFindFunction] = useState<Function>();

  const ff = useCallback(
    (args) => {
      if (findFunction) findFunction(args);
      else console.warn('find function not set');
    },
    [findFunction]
  );

  return (
    <Router hashType='slash'>
      <Switch>
        <Layout
          sidebarContent={sidebarContent}
          setSidebarContent={setSidebarContent}
          findFunction={ff}
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
        </Layout>
      </Switch>
    </Router>
  );
};

export default App;
