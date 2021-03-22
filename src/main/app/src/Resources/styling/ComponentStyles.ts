import { darken, Theme } from '@material-ui/core';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const componentStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      margin: 5,
      marginTop: 0,
      padding: 0,
      backgroundColor: darken(theme.palette.primary.main, 0.3),
      flexShrink: 0,
      overflowY: 'hidden'
    },
    cardHeader: {
      backgroundColor: theme.palette.primary.main,
      padding: 10,
    },
    cardSubheader: {
      fontSize: '0.8rem',
    },
    cardActions: {
      padding: 0,
    },
  })
);

export default componentStyles;
