import React, { useState, useEffect, useContext, ReactElement } from 'react';
import { INotificationMessage } from "../../interfaces";
import { Client, StompSubscription } from '@stomp/stompjs';
import { withBasePath } from '../../utils/API/BasePath';
import IconButton from '@material-ui/core/IconButton';
import makeStyles from '@material-ui/core/styles/makeStyles';
import { Badge, createStyles, Theme } from '@material-ui/core';
import { RssFeed } from '@material-ui/icons';
import { NotificationContext } from '../../Context/NotificationContext';
import Changes from './Changes';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    icon: {
      color: 'rgba(255, 255, 255, 0.75)',
      borderColor: theme.palette.primary.main,
      borderWidth: 1,
      borderStyle: 'solid',
      height: '1.9em',
      width: '1.9em',
      marginLeft: 10,
    },
  })
);

interface Props {
  setSidebarContent: Function;
}

/**
 * Logic component for notifications. Subscribes via websockets to server events.
 *
 *
 */
const Notification: React.FC<Props> = ({ setSidebarContent }) => {

  const classes = useStyles();
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

  if (notificationContext.notification == null) return <></>;

  return (
    <Badge color='secondary' variant='dot' overlap='circle' invisible={!newChanges} title={'Recent changes'}>
      <IconButton
        size={'small'}
        className={classes.icon}
        onClick={() => {
          setNewChanges(false);
          return setSidebarContent(renderedChanges);
        }}
      >
        <RssFeed />
      </IconButton>
    </Badge>
  );
};

export default Notification;
