import { IItem } from '../../../../interfaces';
import React from 'react';
import { getItemIcon } from '../../Utils/utils';
import Avatar from '@material-ui/core/Avatar';
import { Badge, Theme, withStyles } from '@material-ui/core';
import { createStyles } from '@material-ui/core/styles';

interface Props {
  item: IItem;
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
        //animation: '$ripple 1.2s infinite ease-in-out',
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
const ItemAvatar: React.FC<Props> = ({ item, statusColor }) => {
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
        imgProps={{ style: { objectFit: 'contain' } }}
        src={getItemIcon(item)}
        style={{
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          border: '2px solid #' + item.color,
        }}
      />
    </StyledBadge>
  );
};


export default ItemAvatar;