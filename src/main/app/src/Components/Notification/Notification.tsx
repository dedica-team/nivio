import React, { useState, useEffect, useContext, useCallback } from 'react';
import { INotificationMessage } from '../../interfaces';
import { Client, StompSubscription } from '@stomp/stompjs';
import { withBasePath } from '../../utils/API/BasePath';
import IconButton from '@material-ui/core/IconButton';
import makeStyles from '@material-ui/core/styles/makeStyles';
import {
  Badge,
  Card,
  CardHeader,
  createStyles,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Theme,
} from '@material-ui/core';
import { RssFeed } from '@material-ui/icons';
import { Alert } from '@material-ui/lab';
import { NotificationContext } from '../../Context/NotificationContext';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../Resources/styling/ComponentStyles';

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
  const componentClasses = componentStyles();

  const backendUrl = withBasePath('/subscribe');
  const protocol = window.location.protocol !== 'https:' ? 'ws' : 'wss';

  const [socketUrl] = useState(protocol + `://${backendUrl.replace(/^https?:\/\//i, '')}`);
  const [subscriptions, setSubscriptions] = useState<StompSubscription[]>([]);
  const [newChanges, setNewChanges] = useState<Boolean>(false);
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
            console.log(notificationMessage);
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

  const renderNotification = useCallback(() => {
    if (notificationContext.notification == null) return null;

    const notification: INotificationMessage = notificationContext.notification;
    let changes = [];
    if (notification.changelog != null) {
      for (let key of Object.keys(notification.changelog.changes)) {
        let change = notification.changelog.changes[key];
        changes.push(
          <TableRow key={key}>
            <TableCell style={{ width: '33%' }}>
              {change.changeType} {change.componentType}
            </TableCell>
            <TableCell>{key}<br />{change.message}</TableCell>
          </TableRow>
        );
      }
    }
    return (
      <Card className={componentClasses.card}>
        <CardHeader title={notification.landscape}/>
        <CardContent>
          <Alert severity={notification.level}>
            {notification.date} {notification.landscape}
            <br />
            {notification.message}
          </Alert>
          <br />

          <Table aria-label={'changes'} style={{ tableLayout: 'fixed' }}>
            <TableBody>{notification.changelog != null ? changes : null}</TableBody>
          </Table>
        </CardContent>
      </Card>
    );
  }, [notificationContext.notification, componentClasses.card]);

  return (
    <Badge color='secondary' variant='dot' overlap='circle' invisible={!newChanges}>
      <IconButton
        size={'small'}
        className={classes.icon}
        title={'Recent changes'}
        onClick={() => {
          setNewChanges(false);
          return setSidebarContent(renderNotification());
        }}
      >
        <RssFeed />
      </IconButton>
    </Badge>
  );
};

export default Notification;
