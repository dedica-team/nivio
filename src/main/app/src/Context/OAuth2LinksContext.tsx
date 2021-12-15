import React from 'react';
import { ILinks } from '../interfaces';

export interface OAuth2LinksContextType {
  oAuth2Link: ILinks;
}

export const OAuth2LinksContext = React.createContext<OAuth2LinksContextType>({
  oAuth2Link: {},
});

interface Props {
  oAuth2LinksProps: ILinks;
}

const OAuth2LinksProvider: React.FC<Props> = ({ children, oAuth2LinksProps }) => {
  return (
    <OAuth2LinksContext.Provider
      value={{
        oAuth2Link: oAuth2LinksProps,
      }}
    >
      {children}
    </OAuth2LinksContext.Provider>
  );
};
export { OAuth2LinksProvider };
