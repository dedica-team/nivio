import React, { useEffect, useState } from 'react';
import { IAssessment, IAssessmentProps, ILandscape, INotificationMessage } from '../interfaces';
import { get } from '../utils/API/APIClient';
import { withBasePath } from '../utils/API/BasePath';
import { Client, StompSubscription } from '@stomp/stompjs';

export interface LandscapeContextType {
  readonly landscape: ILandscape | null;
  readonly assessment: IAssessment | null;
  readonly identifier: string | null;
  readonly mapChanges: INotificationMessage | null;
  readonly landscapeChanges: INotificationMessage | null;
  next: (identifier: string | null) => void;
  getAssessmentSummary: (fqi: string) => IAssessmentProps | null;
}

export const LandscapeContext = React.createContext<LandscapeContextType>({
  landscape: null,
  assessment: null,
  identifier: null,
  mapChanges: null,
  landscapeChanges: null,
  next: () => {},
  getAssessmentSummary: () => {
    return null;
  },
});

/**
 * Provides the current landscape and assessment.
 *
 * - Responsible for loading objects from the API.
 * - Responsible to fetch change events (subscribes via websockets to server events)
 *
 * @param props
 * @constructor
 */
const LandscapeContextProvider: React.FC = (props) => {
  /**
   * Current landscape identifier. Can be changed through context.
   */
  const [identifier, setIdentifier] = useState<string | null>(null);

  const [landscape, setLandscape] = useState<ILandscape | null>(null);
  const [assessment, setAssessment] = useState<IAssessment | null>(null);
  const [mapChanges, setMapChanges] = useState<INotificationMessage | null>(null);
  const [landscapeChanges, setLandscapeChanges] = useState<INotificationMessage | null>(null);
  const [assessmentChanges, setAssessmentChanges] = useState<INotificationMessage | null>(null);

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
        const eventSubscription = client.subscribe('/topic/events', (m) => {
          const event: INotificationMessage = JSON.parse(m.body);
          console.debug(`Received event: ${event.type}`);
          if (event.type === 'LayoutChangedEvent') {
            setMapChanges(event);
          }

          if (event.type === 'ProcessingFinishedEvent') {
            setLandscapeChanges(event);
          }

          if (event.type === 'AssessmentChangedEvent') {
            setAssessmentChanges(event);
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
   * Load the landscape data when the identifier changes.
   */
  useEffect(() => {
    if (identifier == null) return;

    get(`/api/${identifier}`).then((response) => {
      setLandscape(response);
      console.debug(`Loaded landscape data after identifier change: ${identifier}`);
    });
    setLandscapeChanges(null);
  }, [identifier]);

  /**
   * Load the landscape and assessment data when the identifier changes.
   */
  useEffect(() => {
    if (identifier == null) return;
    setAssessment(null);
    get(`/assessment/${identifier}`).then((response) => {
      setAssessment(response);
      console.debug(`Loaded assessment for ${identifier}`);
    });
  }, [identifier, assessmentChanges]);

  return (
    <LandscapeContext.Provider
      value={{
        landscape: landscape,
        assessment: assessment,
        identifier: identifier,
        mapChanges: mapChanges,
        landscapeChanges: landscapeChanges,
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
