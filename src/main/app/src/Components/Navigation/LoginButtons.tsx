import { FacebookLoginButton, GithubLoginButton } from 'react-social-login-buttons';
import * as React from 'react';
import { useContext } from 'react';
import { OAuth2LinksContext } from '../../Context/OAuth2LinksContext';

export const getLoginButton = (oAuth2LoginService: string | undefined) => {
  switch (oAuth2LoginService) {
    case 'github':
      return <GithubLoginButton />;
    case 'facebook':
      return <FacebookLoginButton />;
  }
};

export const LoginButtons: React.FC = () => {
  const oAuth2Links = useContext(OAuth2LinksContext);
  let loginButtons: JSX.Element[] = [];
  for (const k in oAuth2Links.oAuth2Link) {
    loginButtons.push(
      <a href={oAuth2Links.oAuth2Link[k].href}>{getLoginButton(oAuth2Links.oAuth2Link[k].rel)}</a>
    );
  }
  return <>{loginButtons}</>;
};
