import React, { ReactElement } from 'react';

import Navigation from '../Navigation/Navigation';
import Notification from '../Notification/Notification';
import {darken, Drawer, Theme, Toolbar} from '@material-ui/core';
import { createStyles, makeStyles } from '@material-ui/core/styles';

interface Props {
  children: string | ReactElement | ReactElement[];
  sidebarContent: string | ReactElement | ReactElement[];
  setSidebarContent: Function;
  findFunction: Function;
}

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: 'flex',
    },
    appBar: {
      zIndex: theme.zIndex.drawer + 1,
      position: 'relative',
      backgroundColor: theme.palette.secondary.main,
    },
    drawer: {
      width: 320,
      flexShrink: 0,
    },
    drawerPaper: {
      width: 320,
      backgroundColor: darken(theme.palette.secondary.main, 0.2),
    },
    drawerContainer: {
      overflow: 'auto',
    },
    content: {
      display: 'flex',
      flexDirection: 'row',
      padding: theme.spacing(3),
    },
  })
);

/**
 * Contains our site layout, Navigation on top, content below
 * @param param0
 */
const Layout: React.FC<Props> = ({ children, sidebarContent, setSidebarContent, findFunction }) => {
  const classes = useStyles();

  return (
    <React.Fragment>
      <Navigation
        appBarClass={classes.appBar}
        setSidebarContent={setSidebarContent}
        findFunction={findFunction}
      />
      <div className={classes.content}>
        {children}
        <Drawer
          className={classes.drawer}
          variant='permanent'
          anchor={'right'}
          classes={{
            paper: classes.drawerPaper,
          }}
        >
          <Toolbar />
          {sidebarContent}
        </Drawer>
      </div>

      <Notification />
    </React.Fragment>
  );
};

export default Layout;
