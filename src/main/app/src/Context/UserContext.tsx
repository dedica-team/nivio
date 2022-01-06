import React, { useEffect, useState } from 'react';
import { get } from '../utils/API/APIClient';
import { IUser } from '../interfaces';

export interface UserContextType {
  user?: IUser;
  error?: number;
}

export const UserContext = React.createContext<UserContextType>({

});

const UserProvider: React.FC = ({ children }) => {
  const [user, setUser] = useState<IUser | undefined>();
  const [error, setError] = useState<number | undefined>();

  useEffect(() => {
    get(`/user`).then(
      (response) => {
        setUser(response);
      },
      (errorData) => {
        if (new Error(errorData).message.includes('401')) {
          setError(401);
        } else {
          setError(404);
        }
        console.log(new Error(errorData).message);
      }
    );
  }, []);

  return (
    <UserContext.Provider
      value={{
        user: user,
        error: error,
      }}
    >
      {children}
    </UserContext.Provider>
  );
};
export { UserProvider };
