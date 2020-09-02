import React, { useState, useEffect } from 'react';
import { INotificationMessage } from '../../interfaces';
import { Client, StompSubscription } from '@stomp/stompjs';
import MuiAlert, { AlertProps } from '@material-ui/lab/Alert';
import Button from '@material-ui/core/Button';
import Snackbar from '@material-ui/core/Snackbar';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';
import dateFormat from 'dateformat';

import { Link } from 'react-router-dom';

function Alert(props: AlertProps) {
  return <MuiAlert elevation={6} variant='filled' {...props} />;
}

export interface SnackbarMessage {
  message: string;
  key: number;
  landscape: string;
  level: 'success' | 'info' | 'warning' | 'error' | undefined;
}

/**
 * Displays a notification if a file change is detected
 */
const Notification: React.FC = () => {
  const snackPackCloseDelay = 2000000;
  const [snackPack, setSnackPack] = useState<SnackbarMessage[]>([]);
  const [messageInfo, setMessageInfo] = useState<SnackbarMessage | undefined>(undefined);
  const [open, setOpen] = useState(false);
  const [backendUrl] = useState(
    process.env.REACT_APP_BACKEND_URL?.replace(/^https?:\/\//i, '') ||
      `${window.location.hostname}:${window.location.port}`
  );
  const [socketUrl] = useState(`ws://${backendUrl}/subscribe`);
  const [subscriptions, setSubscriptions] = useState<StompSubscription[]>([]);
  const [client] = useState(
    new Client({
      brokerURL: socketUrl,
      onConnect: () => {
        const subscriptions: StompSubscription[] = [];
        const eventSubscription = client.subscribe('/topic/events', (message) => {
          const notificationMessage: INotificationMessage = JSON.parse(message.body);
          const formattedMessage = notificationMessage.message
            ? `Message: ${notificationMessage.message}`
            : '';
          const snackPackMessage = {
            message: `${dateFormat(notificationMessage.date, 'hh:MM:ss TT')}: Change in ${
              notificationMessage.landscape
            } ${formattedMessage}`,
            key: new Date().getTime(),
            landscape: notificationMessage.landscape,
            level: notificationMessage.level,
          };
          setSnackPack((prevArray) => [...prevArray, snackPackMessage]);
          setOpen(true);
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
    <div className={'notification'}>
      <Snackbar
        key={messageInfo ? messageInfo.key : undefined}
        open={open}
        autoHideDuration={snackPackCloseDelay}
        onClose={handleClose}
        onExited={handleExited}
      >
        <Alert
          onClose={handleClose}
          severity={messageInfo?.level}
          action={
            <React.Fragment>
              <Button component={Link} to={`/landscape/${messageInfo?.landscape}`}>
                Show Map
              </Button>
              <IconButton aria-label='close' color='inherit' onClick={handleClose}>
                <CloseIcon />
              </IconButton>
            </React.Fragment>
          }
        >
          {messageInfo ? messageInfo.message : undefined}
        </Alert>
      </Snackbar>
    </div>
  );
};

export default Notification;
