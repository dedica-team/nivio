import { Theme } from '@material-ui/core';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const componentStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      margin: 5,
      marginTop: 0,
      padding: 0,
      backgroundColor: theme.palette.secondary.dark,
    },
    cardHeader: {
      backgroundColor: theme.palette.secondary.main,
      padding: 10,
    },
    cardSubheader: {
      fontSize: '0.8rem',
    },
    cardActions: {
      padding: 0,
    },
    icon: {
      height: '2em',
    },
    itemAvatar: {
      width: theme.spacing(3),
      height: theme.spacing(3),
    },
    floatingButton: {
      color: theme.palette.primary.contrastText,
    },
  })
);

export default componentStyles;
