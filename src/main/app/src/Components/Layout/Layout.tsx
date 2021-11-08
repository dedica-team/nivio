import React, { ReactElement } from 'react';

import Navigation from '../Navigation/Navigation';
import { Button, Drawer, Theme } from "@material-ui/core";
import { createStyles, makeStyles } from '@material-ui/core/styles';

interface Props {
  children: string | ReactElement | ReactElement[];
  sidebarContent: string | ReactElement | ReactElement[];
  setSidebarContent: Function;
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
  sidebarContent,
  setSidebarContent,
  pageTitle,
  logo,
  version,
}) => {
  const classes = useStyles();

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
        style={{ width: sidebarContent != null ? searchSupportWidth : 0 }}
        anchor={'right'}
        variant={'persistent'}
        open={sidebarContent != null}
        onClose={() => {
          setSidebarContent(null);
        }}
        children={sidebarContent}
      />
    </div>
  );
};

export default Layout;
