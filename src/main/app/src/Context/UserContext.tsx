import React, { useEffect, useState } from 'react';
import { get } from '../utils/API/APIClient';

export interface UserContextType {
  user: string;
}

export const UserContext = React.createContext<UserContextType>({
  user: '',
});

const UserProvider: React.FC = ({ children }) => {
  const [user, setUser] = useState('');

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
