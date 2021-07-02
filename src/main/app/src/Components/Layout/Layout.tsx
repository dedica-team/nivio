import React, { ReactElement } from "react";

import Navigation from "../Navigation/Navigation";
import { Drawer, Theme } from "@material-ui/core";
import { createStyles, makeStyles } from "@material-ui/core/styles";
import Search from "../Landscape/Search/Search";

interface Props {
  children: string | ReactElement | ReactElement[];
  sidebarContent: string | ReactElement | ReactElement[];
  setSidebarContent: Function;
  pageTitle?: string;
  logo?: string;
}

const searchSupportWidth = 360;
const sidebarWidth = 280;

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    root: {
      display: "flex"
    },
    sideBar: {
      position: "absolute",
      right: 0,
      top: 5,
      width: sidebarWidth,
      overflow: "auto",
      maxHeight: "calc(100vh - 50px)",
      zIndex: 5000
    },

    outer: {
      display: "flex",
      flexDirection: "row"
    },
    content: {
      position: 'relative'
    },
    flexItem: {
      flexShrink: 1,
      flexGrow: 1
    },
    main: {
      flexShrink: 1,
      flexGrow: 2,
      width: "1000px"
    },
    searchSupport: {
      backgroundColor: theme.palette.primary.dark,
      width: searchSupportWidth,
      padding: 5
    }
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
                                   logo
                                 }) => {
  const classes = useStyles();
  const [searchSupport, setSearchSupport] = React.useState<boolean>(false);

  return (
    <div className={classes.outer}>
      <main className={classes.main}>
        <Navigation
          logo={logo}
          setSidebarContent={setSidebarContent}
          setSearchSupport={setSearchSupport}
          searchSupport={searchSupport}
          pageTitle={pageTitle}
        />
        <div className={classes.content}>
          <div className={classes.sideBar}>{sidebarContent}</div>
          {children}
        </div>
      </main>
      <Drawer
        classes={{
          paper: classes.searchSupport
        }}
        style={{ width: searchSupport ? searchSupportWidth : 0 }}
        anchor={"right"}
        variant={"persistent"}
        open={searchSupport}
        onClose={() => {
          setSearchSupport(false);
        }}
      >
        <Search setSidebarContent={setSidebarContent} showSearch={setSearchSupport} />
      </Drawer>
    </div>
  );
};

export default Layout;
