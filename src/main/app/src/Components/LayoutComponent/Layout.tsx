import React, { ReactElement } from 'react';

import Navigation from '../NavigationComponent/Navigation';
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
    </React.Fragment>
  );
};

export default Layout;
