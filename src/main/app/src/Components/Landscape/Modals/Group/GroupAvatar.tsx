import { IGroup } from '../../../../interfaces';
import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import StatusBadge from '../../Utils/StatusBadge';

interface Props {
  group: IGroup;
  statusColor: string;
}

/**
 * Returns a chosen group if information is available
 */
const GroupAvatar: React.FC<Props> = ({ group, statusColor }) => {
  const classes = componentStyles();

  return (
    <StatusBadge
      overlap={'circular'}
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
          paddingLeft: 1.5,
        }}
        src={group.icon}
      >
        {group.identifier[0].toUpperCase()}
      </Avatar>
    </StatusBadge>
  );
};

export default GroupAvatar;
