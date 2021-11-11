import React, { ReactElement, useEffect, useState } from 'react';

import Navigation from '../Navigation/Navigation';
import { Drawer, Theme } from '@material-ui/core';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';
import { CloseSharp } from '@material-ui/icons';

interface Props {
  children: string | ReactElement | ReactElement[];
  pageTitle?: string;
  logo?: string;
  version?: string;
}

const searchSupportWidth = 360;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
    outer: {},
    flexItem: {
      flexShrink: 1,
      flexGrow: 1,
    },
    main: {
      display: 'flex',
      flexDirection: 'row',
    },
    children: {
      flexShrink: 1,
      flexGrow: 2,
      width: '1000px',
    },
    sidebar: {
      backgroundColor: theme.palette.primary.dark,
      width: searchSupportWidth,
      padding: 5,
      top: 0,
      marginTop: -30,
      position: 'absolute',
    },
  })
);

/**
 * Contains our site layout, Navigation on top, content below
 */
const Layout: React.FC<Props> = ({ children, pageTitle, logo, version }) => {
  const classes = useStyles();
  const [sidebarContent, setSidebarContent] = useState<ReactElement | null>(null);
  const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

  useEffect(() => {
    const isOpen = sidebarContent != null;
    console.log('isOpen', isOpen);
    setSidebarOpen(isOpen);
  }, [sidebarContent]);

  return (
    <div className={classes.outer}>
      <Navigation
        logo={logo}
        version={version}
        setSidebarContent={setSidebarContent}
        pageTitle={pageTitle}
      />
      <main className={classes.main}>
        <div className={classes.children}>{children}</div>
        <Drawer
          classes={{
            paper: classes.sidebar,
          }}
          style={{
            width: sidebarOpen ? searchSupportWidth : 0,
            marginTop: 30,
            position: 'relative',
          }}
          anchor={'right'}
          variant={'persistent'}
          open={sidebarOpen}
        >
          <div style={{ position: 'absolute', right: '0.5em' }}>
            <IconButton onClick={() => setSidebarOpen(false)} size={'small'}>
              <CloseSharp />
            </IconButton>
          </div>
          {sidebarContent}
        </Drawer>
      </main>
    </div>
  );
};

export default Layout;
