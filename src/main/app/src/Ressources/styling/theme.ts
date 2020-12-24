import { createMuiTheme } from '@material-ui/core/styles';

const theme = createMuiTheme({
  palette: {
    type: 'dark',
    background: {
      default: '#161618'
    },
    primary: { main: 'rgb(0, 104, 104)'},
    secondary: {
      main: 'rgb(0, 104, 104)',
      dark: '#333',
    },
  },
  zIndex: {
    drawer: 1000
  },
  typography: {

    h3 : {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)'
    },
    h4 : {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)'
    },
    h5 : {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)'
    },
    h6 : {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      color: 'rgba(255, 255, 255, 0.75)'
    }
  },
});
export default theme;
