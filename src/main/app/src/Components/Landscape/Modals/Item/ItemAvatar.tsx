import { IItem } from '../../../../interfaces';
import React from 'react';
import { getItemIcon } from '../../Utils/utils';
import Avatar from '@material-ui/core/Avatar';
import StatusBadge from "../../Utils/StatusBadge";

interface Props {
  item: IItem;
  statusColor: string;
}

/**
 * Returns a chosen group if information is available
 */
const ItemAvatar: React.FC<Props> = ({ item, statusColor }) => {
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
        imgProps={{ style: { objectFit: 'contain' } }}
        src={getItemIcon(item)}
        style={{
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          border: '2px solid #' + item.color,
        }}
      />
    </StatusBadge>
  );
};


export default ItemAvatar;