import React, { useState, useEffect } from 'react';
import { INotificationMessage, ISnackbarMessage } from '../../interfaces';
import { Client, StompSubscription } from '@stomp/stompjs';

import './Notification.scss';
import NotificationLayout from './NotificationLayout';

import { withBasePath } from '../../utils/API/BasePath';

/**
 * Logic component for notifications
 */
const Notification: React.FC = () => {
  const snackPackCloseDelay = 20000;
  const [snackPack, setSnackPack] = useState<ISnackbarMessage[]>([]);
  const [messageInfo, setMessageInfo] = useState<ISnackbarMessage | undefined>(undefined);
  const [open, setOpen] = useState(false);
  const backendUrl = withBasePath('/subscribe');
  const protocol = window.location.protocol !== 'https:' ? 'ws' : 'wss';
  const [socketUrl] = useState(protocol + `://${backendUrl.replace(/^https?:\/\//i, '')}`);
  const [subscriptions, setSubscriptions] = useState<StompSubscription[]>([]);
  const [client] = useState(
    new Client({
      brokerURL: socketUrl,
      onConnect: () => {
        const subscriptions: StompSubscription[] = [];
        const eventSubscription = client.subscribe('/topic/events', (message) => {
          const notificationMessage: INotificationMessage = JSON.parse(message.body);
          if (notificationMessage.type !== 'ProcessingFinishedEvent') {
            const snackPackMessage = {
              message: notificationMessage.landscape
                ? `Change in ${notificationMessage.landscape}, ${notificationMessage.message || ''}`
                : `${notificationMessage.message || 'Event Error: No message received'}`,
              key: new Date().getTime(),
              landscape: notificationMessage.landscape,
              level: notificationMessage.level,
            };
            setSnackPack((prevArray) => [...prevArray, snackPackMessage]);
            setOpen(true);
          }
        });

        subscriptions.push(eventSubscription);
        setSubscriptions(subscriptions);
      },
    })
  );

  useEffect(() => {
    if (snackPack.length && !messageInfo) {
      // Set a new snack when we don't have an active one
      setMessageInfo({ ...snackPack[0] });
      setSnackPack((prev) => prev.slice(1));
      setOpen(true);
    } else if (snackPack.length && messageInfo && open) {
      // Close an active snack when a new one is added and the close delay is over
      if (new Date().getTime() - messageInfo.key > snackPackCloseDelay) {
        setOpen(false);
      }
    }
  }, [snackPack, messageInfo, open]);

  useEffect(() => {
    client.activate();
    return () => {
      subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
    };
  }, [client, subscriptions]);

  const handleClose = (event?: React.SyntheticEvent, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }

    setOpen(false);
  };

  const handleExited = () => {
    setMessageInfo(undefined);
  };

  return (
    <div className={'notification'} data-testid='notification'>
      <NotificationLayout
        handleClose={handleClose}
        handleExited={handleExited}
        messageInfo={messageInfo}
        open={open}
        snackPackCloseDelay={snackPackCloseDelay}
      />
    </div>
  );
};

export default Notification;
