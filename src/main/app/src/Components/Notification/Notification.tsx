import React, { useState, useEffect, useContext, ReactElement } from 'react';
import { INotificationMessage } from "../../interfaces";
import { Client, StompSubscription } from '@stomp/stompjs';
import { withBasePath } from '../../utils/API/BasePath';
import IconButton from '@material-ui/core/IconButton';
import { Badge} from '@material-ui/core';
import { DynamicFeed } from "@material-ui/icons";
import { NotificationContext } from '../../Context/NotificationContext';
import Changes from './Changes';
import componentStyles from "../../Resources/styling/ComponentStyles";

interface Props {
  setSidebarContent: Function;
}

/**
 * Logic component for notifications. Subscribes via websockets to server events.
 *
 *
 */
const Notification: React.FC<Props> = ({ setSidebarContent }) => {

  const classes = componentStyles();
  const backendUrl = withBasePath('/subscribe');
  const protocol = window.location.protocol !== 'https:' ? 'ws' : 'wss';

  const [socketUrl] = useState(protocol + `://${backendUrl.replace(/^https?:\/\//i, '')}`);
  const [subscriptions, setSubscriptions] = useState<StompSubscription[]>([]);
  const [newChanges, setNewChanges] = useState<Boolean>(false);
  const [renderedChanges, setRenderedChanges] = useState<ReactElement | null>(null);
  const notificationContext = useContext(NotificationContext);

  const [client] = useState(
    new Client({
      brokerURL: socketUrl,
      onConnect: () => {
        const subscriptions: StompSubscription[] = [];
        const eventSubscription = client.subscribe('/topic/events', (message) => {
          const notificationMessage: INotificationMessage = JSON.parse(message.body);
          if (notificationMessage.type === 'ProcessingFinishedEvent') {
            notificationContext.next(notificationMessage);
            setNewChanges(true);
          }
        });

        subscriptions.push(eventSubscription);
        setSubscriptions(subscriptions);
      },
    })
  );

  useEffect(() => {
    client.activate();
    return () => {
      subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
    };
  }, [client, subscriptions]);

  /**
   * render changes,
   */
  useEffect(() => {
    if (notificationContext.notification == null) return;
    setRenderedChanges(<Changes notification={notificationContext.notification} />);
  }, [notificationContext.notification]);

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
        <DynamicFeed />
      </IconButton>
    </Badge>
  );
};

export default Notification;
