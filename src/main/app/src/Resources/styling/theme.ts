import { ThemeOptions } from '@material-ui/core/styles';

const defaultThemeVariables: ThemeOptions = {
  palette: {
    type: 'dark',
    background: {
      default: '#161618',
    },

    primary: {
      main: 'rgb(0,104,104)',
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
    },
    h4: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
    },
    h5: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
    },
    h6: {
      fontFamily: 'monospace',
      textTransform: 'uppercase',
      fontSize: '1rem'
    },
  },
  overrides: {
    MuiTableCell: {
      root: {  //This can be referred from Material UI API documentation.
        padding: '5px',
      },
    },
    MuiCssBaseline: {
      "@global": {
        "*::-webkit-scrollbar": {
          maxWidth: "5px"
        },
        "*::-webkit-scrollbar-thumb": {
          backgroundColor: 'rgba(255, 255, 255, 0.75)'
        },
        "*:hover": {
          "&::-webkit-scrollbar-thumb": {
            backgroundColor: 'rgba(255, 255, 255, 0.75)'
          }
        }
      }
    }
  }
};
export default defaultThemeVariables;
