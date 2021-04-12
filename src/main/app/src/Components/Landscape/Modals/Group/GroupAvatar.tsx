import { IGroup } from '../../../../interfaces';
import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import { Badge, Theme, withStyles } from '@material-ui/core';
import { createStyles } from '@material-ui/core/styles';
import componentStyles from '../../../../Resources/styling/ComponentStyles';

interface Props {
  group: IGroup;
  statusColor: string;
}

const StyledBadge = withStyles((theme: Theme) =>
  createStyles({
    'badge': {
      'boxShadow': `0 0 0 2px ${theme.palette.background.paper}`,
      '&::after': {
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        borderRadius: '50%',
        border: '1px solid currentColor',
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

/**
 * Returns a chosen group if information is available
 */
const GroupAvatar: React.FC<Props> = ({ group, statusColor }) => {
  const classes = componentStyles();

  return (
    <StyledBadge
      overlap={'circle'}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'right',
      }}
      variant={'dot'}
      style={{ color: statusColor }}
    >
      <Avatar
        className={classes.groupAvatar}
        title={'Click to highlight the group.'}
        style={{
          backgroundColor: '#' + group.color,
          paddingTop: 6,
        }}
      >
        {group.identifier[0].toUpperCase()}
      </Avatar>
    </StyledBadge>
  );
};


export default GroupAvatar;