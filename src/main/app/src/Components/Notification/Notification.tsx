import React, { useState, useEffect, useContext } from "react";
import { INotificationMessage } from '../../interfaces';
import { Client, StompSubscription } from '@stomp/stompjs';
import { withBasePath } from '../../utils/API/BasePath';
import { NotificationContext } from "../../Context/NotificationContext";

/**
 * Logic component for notifications. Subscribes via websockets to server events.
 *
 *
 */
const Notification: React.FC = () => {

  const backendUrl = withBasePath('/subscribe');
  const protocol = window.location.protocol !== 'https:' ? 'ws' : 'wss';

  const [socketUrl] = useState(protocol + `://${backendUrl.replace(/^https?:\/\//i, '')}`);
  const [subscriptions, setSubscriptions] = useState<StompSubscription[]>([]);
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

  return null;
};

export default Notification;
