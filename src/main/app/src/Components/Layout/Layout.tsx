import React, { ReactElement, useEffect, useState } from "react";

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
    outer: {
      display: 'flex',
      flexDirection: 'row',
    },
    flexItem: {
      flexShrink: 1,
      flexGrow: 1,
    },
    main: {
      flexShrink: 1,
      flexGrow: 2,
      width: '1000px',
    },
    sidebar: {
      backgroundColor: theme.palette.primary.dark,
      width: searchSupportWidth,
      padding: 5,
    },
  })
);

/**
 * Contains our site layout, Navigation on top, content below
 * @param param0
 */
const Layout: React.FC<Props> = ({
  children,
  pageTitle,
  logo,
  version,
}) => {
  const classes = useStyles();
  const [sidebarContent, setSidebarContent] = useState<ReactElement[]>([]);
  const [sidebarOpen, setSidebarOpen] = useState<boolean>(false);

  useEffect(() => {
    setSidebarOpen(sidebarContent != null);
  }, [sidebarContent]);

  return (
    <div className={classes.outer}>
      <main className={classes.main}>
        <Navigation
          logo={logo}
          version={version}
          setSidebarContent={setSidebarContent}
          pageTitle={pageTitle}
        />
        {children}
      </main>
      <Drawer
        classes={{
          paper: classes.sidebar,
        }}
        style={{ width: sidebarOpen ? searchSupportWidth : 0 }}
        anchor={'right'}
        variant={'persistent'}
        open={sidebarOpen}
      >
        <div style={{position: 'absolute', right: '0.5em'}}>
          <IconButton onClick={() => setSidebarOpen(false)} size={"small"}>
            <CloseSharp />
          </IconButton>
        </div>
        {sidebarContent}
      </Drawer>
    </div>
  );
};

export default Layout;
