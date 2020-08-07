import React, { useState, useEffect } from 'react';
import './Notification.scss';
import { INotificationMessage } from '../../interfaces';
import { Client, StompSubscription } from '@stomp/stompjs';

/**
 * Displays a notification if a file change is detected
 */
const Notification: React.FC = () => {
  const [messages, setMessages] = useState<INotificationMessage[]>([]);
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
          setMessages((prevArray) => [...prevArray, notificationMessage]);
          setOpen(true);
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

  const closeNotification = () => {
    setOpen(false);
    setTimeout(() => {
      setMessages([]);
    }, 3000);
  };

  return (
    <div className={open ? 'notification open' : 'notification closed'}>
      <div className='notificationMessageContainer'>
        {messages.map((message) => {
          return (
            <span className='notificationMessage newMessage' key={message.timestamp + message.type}>
              {JSON.stringify(message)}
            </span>
          );
        })}
      </div>
      <button className={'close'} onClick={closeNotification}>
        X
      </button>
    </div>
  );
};

export default Notification;
