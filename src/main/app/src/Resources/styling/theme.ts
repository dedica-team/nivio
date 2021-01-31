import { ThemeOptions } from '@material-ui/core/styles';

const defaultThemeVariables: ThemeOptions = {
  palette: {
    type: 'dark',
    background: {
      default: '#161618',
    },
    text: {
      primary: 'rgba(255, 255, 255, 0.75)',
    },
    primary: {
      main: 'rgb(0,104,104)',
      contrastText: 'rgba(255, 255, 255, 0.75)',
    },
    secondary: {
      main: 'rgb(0, 104, 104)',
      dark: '#333',
    },
  },
  zIndex: {
    drawer: 1000,
  },
  typography: {
    h3: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)',
    },
    h4: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)',
    },
    h5: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)',
    },
    h6: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)',
    },
  },
};
export default defaultThemeVariables;
