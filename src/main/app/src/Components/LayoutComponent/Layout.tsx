import React, { ReactElement } from 'react';

import Navigation from '../NavigationComponent/Navigation';
import Notification from '../NotificationComponent/Notification';
import './Layout.scss';

interface Props {
  children: string | ReactElement | ReactElement[];
}

/**
 * Contains our site layout, Navigation on top, content below
 * @param param0
 */
const Layout: React.FC<Props> = ({ children }) => {
  return (
    <React.Fragment>
      <Navigation />
      <div className='content'>{children}</div>
      <Notification />
    </React.Fragment>
  );
};

export default Layout;
