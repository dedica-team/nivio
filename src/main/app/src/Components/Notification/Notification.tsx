import React, { ReactElement, useContext, useEffect, useState } from 'react';
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
  const [newChanges, setNewChanges] = useState<Boolean>(false);
  const [renderedChanges, setRenderedChanges] = useState<ReactElement | null>(null);
  const landscapeContext = useContext(LandscapeContext);
  

  /**
   * render changes,
   */
  useEffect(() => {
    if (landscapeContext.notification == null) return;
    setRenderedChanges(<Changes notification={landscapeContext.notification} />);
    setNewChanges(true);
  }, [landscapeContext.notification]);

  return (
    <Badge color='secondary' variant='dot' overlap='circle' invisible={!newChanges} title={'Recent changes'}>
      <IconButton
        size={'small'}
        className={classes.navigationButton}
        onClick={() => {
          setNewChanges(false);
          return setSidebarContent(renderedChanges);
        }}
      >
        <Notifications />
      </IconButton>
    </Badge>
  );
};

export default Notification;
