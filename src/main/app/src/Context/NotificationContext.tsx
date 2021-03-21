import React, { useState } from 'react';
import { INotificationMessage } from "../interfaces";

export interface NotificationContextType {
  notification: INotificationMessage | null;
  next: (notification: INotificationMessage) => void;
}

export const NotificationContext = React.createContext<NotificationContextType>({
  notification:  null,
  next: () => {},
});

/**
 * Visit https://reactjs.org/docs/context.html to read about how to implement and use the context in React.
 */
const NotificationContextProvider: React.FC<{}> = (props) => {
  const [notification, setNotification] = useState<INotificationMessage | null>(null);

  return (
    <NotificationContext.Provider
      value={{
        notification: notification,
        next: setNotification,
      }}
    >
      {props.children}
    </NotificationContext.Provider>
  );
};

export { NotificationContextProvider };
