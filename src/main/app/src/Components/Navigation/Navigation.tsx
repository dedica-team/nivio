import React from 'react';
import { Typography, Theme, createStyles, Box, Menu, MenuItem, withStyles, MenuProps } from "@material-ui/core";
import { Link } from 'react-router-dom';

import Toolbar from '@material-ui/core/Toolbar';

import makeStyles from '@material-ui/core/styles/makeStyles';
import Search from '../Landscape/Search/Search';
import IconButton from '@material-ui/core/IconButton';
import Avatar from '@material-ui/core/Avatar';
import { withBasePath } from "../../utils/API/BasePath";
import Notification from "../Notification/Notification";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    grow: {
      flexGrow: 1,
    },
    pageTitle: {
      padding: 11,
      paddingLeft: 16,
      paddingRight: 16,
    },
    menuIcon: {
      color: 'rgba(255, 255, 255, 0.75)',
      backgroundColor: theme.palette.primary.main,
      height: '1.9em',
      width: '1.9em',
    },
    logo: {
      height: '1.5em',
      width: '1.5em',
    },
    appBar: {
      zIndex: theme.zIndex.drawer + 1,
      position: 'relative',
      backgroundColor: 'transparent',
    }
  })
);

interface Props {
  setSidebarContent: Function;
  pageTitle?: string;
  logo?: string;
}

/**
 * Header Component
 */
const Navigation: React.FC<Props> = ({ setSidebarContent, pageTitle, logo }) => {
  const classes = useStyles();

  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const openMenu = (event: React.MouseEvent<HTMLButtonElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const StyledMenu = withStyles((theme: Theme) => createStyles({
    paper: {
      backgroundColor: theme.palette.primary.main,
      marginTop: 5
    },
  }))((props: MenuProps) => (
    <Menu
      elevation={0}
      getContentAnchorEl={null}
      anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
      transformOrigin={{ vertical: "top", horizontal: "center" }}
      {...props}
    />
  ));
  return (
      <Toolbar >
        <IconButton size={'small'} edge="start" color="inherit" aria-controls="simple-menu" aria-haspopup="true" onClick={openMenu} className={classes.menuIcon}>
          {logo ? (
            <Avatar
              className={classes.logo}
              imgProps={{ style: { objectFit: 'contain' } }}
              src={logo}
            />
          ) : (
            <Avatar
              className={classes.logo}
              imgProps={{ style: { objectFit: 'contain' } }}
              src={withBasePath('icons/svg/nivio.svg')}
            />
          )}
        </IconButton>

        <StyledMenu
          anchorEl={anchorEl}
          keepMounted
          open={Boolean(anchorEl)}
          onClose={handleClose}
        >
          <MenuItem component={Link} to={``} onClick={handleClose}>Home</MenuItem>
          <MenuItem component={Link} to={`/man/install.html`} onClick={handleClose}>Help</MenuItem>
        </StyledMenu>
        <Box className={classes.pageTitle}>
          <Typography variant='h6'>{pageTitle}</Typography>
        </Box>
        <div className={classes.grow} />
        <Search setSidebarContent={setSidebarContent} />{' '}
        <Notification setSidebarContent={setSidebarContent} />
      </Toolbar>
  );
};

export default Navigation;
