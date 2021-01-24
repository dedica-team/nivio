import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import * as serviceWorker from './serviceWorker';
import theme from './Resources/styling/theme';
import { ThemeProvider } from '@material-ui/core/styles';
import { CssBaseline } from '@material-ui/core';
import { LocateFunctionContextProvider } from './Context/LocateFunctionContext';

ReactDOM.render(
  <ThemeProvider theme={theme}>
    <CssBaseline>
      <LocateFunctionContextProvider>
        <App />
      </LocateFunctionContextProvider>
    </CssBaseline>
  </ThemeProvider>,
  document.getElementById('root')
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
