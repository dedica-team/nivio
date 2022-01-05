import React, { useContext, useEffect, useState } from 'react';
import IconButton from '@material-ui/core/IconButton';
import { Badge } from '@material-ui/core';
import { Notifications } from '@material-ui/icons';
import Changes from './Changes';
import componentStyles from '../../Resources/styling/ComponentStyles';
import { LandscapeContext } from '../../Context/LandscapeContext';

interface Props {
  setSidebarContent: Function;
}

/**
 * Displaying server side notifications.
 *
 *
 */
const Notification: React.FC<Props> = ({ setSidebarContent }) => {
  const classes = componentStyles();
  const [newChanges, setNewChanges] = useState<boolean>(false);
  const landscapeContext = useContext(LandscapeContext);

  /**
   * render changes,
   */
  useEffect(() => {
    if (landscapeContext.landscapeChanges == null) return;
    setNewChanges(true);
  }, [landscapeContext.landscapeChanges]);

  return (
    <Badge
      color='secondary'
      variant='dot'
      overlap='circular'
      invisible={!newChanges}
      title={'Recent changes'}
    >
      <IconButton
        size={'small'}
        className={classes.navigationButton}
        onClick={() => {
          setNewChanges(false);
          return setSidebarContent(<Changes />);
        }}
      >
        <Notifications />
      </IconButton>
    </Badge>
  );
};

export default Notification;
