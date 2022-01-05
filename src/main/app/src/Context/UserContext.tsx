import React, { useEffect, useState } from 'react';
import { get } from '../utils/API/APIClient';
import { IUser } from '../interfaces';

export interface UserContextType {
  user?: IUser;
}

export const UserContext = React.createContext<UserContextType>({

});

const UserProvider: React.FC = ({ children }) => {
  const [user, setUser] = useState<IUser | undefined>();

  useEffect(() => {
    get(`/user`).then((response) => {
      setUser(response);
    });
  }, []);

  return (
    <UserContext.Provider
      value={{
        user: user,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
export { UserProvider };
