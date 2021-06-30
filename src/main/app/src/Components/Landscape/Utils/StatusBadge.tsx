import { Badge, Theme, withStyles } from '@material-ui/core';
import { createStyles } from '@material-ui/core/styles';

/**
 * Displays a little status-colored dot over icons.
 *
 *
 */
const StatusBadge = withStyles((theme: Theme) =>
  createStyles({
    'badge': {
      //'boxShadow': `0 0 0 1px ${theme.palette.background.paper}`,
      '&::after': {
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        borderRadius: '50%',
        //animation: '$ripple 1.2s infinite ease-in-out',
        //border: '1px solid currentColor',
        backgroundColor: 'currentColor',
        content: '""',
      },
    },
    '@keyframes ripple': {
      '0%': {
        transform: 'scale(.8)',
        opacity: 1,
      },
      '100%': {
        transform: 'scale(2.4)',
        opacity: 0,
      },
    },
  })
)(Badge);

export default StatusBadge;