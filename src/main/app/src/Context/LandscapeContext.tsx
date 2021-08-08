import React, { useEffect, useState } from 'react';
import { IAssessment, IAssessmentProps, ILandscape, INotificationMessage } from '../interfaces';
import { get } from '../utils/API/APIClient';
import { withBasePath } from '../utils/API/BasePath';
import { Client, StompSubscription } from '@stomp/stompjs';

export interface LandscapeContextType {
  readonly landscape: ILandscape | null;
  readonly assessment: IAssessment | null;
  readonly identifier: string | null;
  readonly notification: INotificationMessage | null;
  next: (identifier: string | null) => void;
  getAssessmentSummary: (fqi: string) => IAssessmentProps | null;
}

export const LandscapeContext = React.createContext<LandscapeContextType>({
  landscape: null,
  assessment: null,
  identifier: null,
  notification: null,
  next: () => {},
  getAssessmentSummary: () => {
    return null;
  },
});

/**
 * Provides the current landscape and assessment.
 *
 * - Responsible for loading objects from the API.
 * - Responsible to fetch change events (notification, subscribes via websockets to server events)
 *
 * @param props
 * @constructor
 */
const LandscapeContextProvider: React.FC<{}> = (props) => {
  const [landscape, setLandscape] = useState<ILandscape | null>(null);
  const [assessment, setAssessment] = useState<IAssessment | null>(null);
  const [notification, setNotification] = useState<INotificationMessage | null>(null);
  const [identifier, setIdentifier] = useState<string | null>(null);

  const backendUrl = withBasePath('/subscribe');
  const protocol = window.location.protocol !== 'https:' ? 'ws' : 'wss';

  const [socketUrl] = useState(protocol + `://${backendUrl.replace(/^https?:\/\//i, '')}`);
  const [subscriptions, setSubscriptions] = useState<StompSubscription[]>([]);

  /**
   * Subscribe to event stream
   */
  const [client] = useState(
    new Client({
      brokerURL: socketUrl,
      onConnect: () => {
        const subscriptions: StompSubscription[] = [];
        const eventSubscription = client.subscribe('/topic/events', (message) => {
          const notificationMessage: INotificationMessage = JSON.parse(message.body);
          if (notificationMessage.type === 'LayoutChangedEvent') {
            setNotification(notificationMessage);
          }
        });

        subscriptions.push(eventSubscription);
        setSubscriptions(subscriptions);
      },
    })
  );

  /**
   * Activate client
   */
  useEffect(() => {
    client.activate();
    return () => {
      subscriptions.forEach((subscription) => {
        subscription.unsubscribe();
      });
    };
  }, [client, subscriptions]);

  /**
   * Load the landscape and assessment data when the identifier changes.
   */
  useEffect(() => {
    if (identifier == null) {
      console.debug(`Identifier not present`);
      return;
    }

    get(`/api/${identifier}`).then((response) => {
      setLandscape(response);
      console.debug(`Loaded landscape data after identifier change: ${identifier}`);
    });
    get(`/assessment/${identifier}`).then((response) => {
      setAssessment(response);
      console.debug(`Loaded assessment data after identifier change: ${identifier}`);
    });
  }, [identifier]);

  /**
   * Load the assessment data on a notification
   */
  useEffect(() => {
    if (identifier == null) {
      console.debug(`Identifier not present`);
      return;
    }

    get(`/assessment/${identifier}`).then((response) => {
      console.log(`Loaded assessment data for ${identifier}`, response);
      setAssessment(response);
    });
  }, [identifier, notification]);

  return (
    <LandscapeContext.Provider
      value={{
        landscape: landscape,
        assessment: assessment,
        identifier: identifier,
        notification: notification,
        next: (nextId) => {
          if (identifier === nextId) return;
          console.debug('New identifier', nextId);
          setIdentifier(nextId);
        },

        getAssessmentSummary: (fqi) => {
          if (!assessment) return null;
          const assessmentResults = assessment.results[fqi];
          if (!assessmentResults) return null;

          return assessmentResults.find((assessmentResult) => assessmentResult.summary) || null;
        },
      }}
    >
      {props.children}
    </LandscapeContext.Provider>
  );
};

export { LandscapeContextProvider };
