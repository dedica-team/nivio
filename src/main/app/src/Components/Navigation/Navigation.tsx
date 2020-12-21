import React from 'react';
import { Button, Typography, AppBar, Theme, createStyles, Box, darken } from '@material-ui/core';
import { Link } from 'react-router-dom';

import Toolbar from '@material-ui/core/Toolbar';
import FavoriteIcon from '@material-ui/icons/Favorite';

import makeStyles from '@material-ui/core/styles/makeStyles';
import Search from '../Landscape/Search/Search';
import { HelpOutlineRounded } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    grow: {
      flexGrow: 1,
    },
    title: {
      marginRight: '20px',
      color: 'rgba(255, 255, 255, 0.75)',
    },
    pageTitle: {
      padding: 8,
      paddingLeft: 16,
      paddingRight: 16,
      backgroundColor: darken(theme.palette.secondary.main, 0.2),
    },
  })
);

interface Props {
  appBarClass: string;
  setSidebarContent: Function;
  findFunction: Function;
  pageTitle?: string;
}

/**
 * Header Component
 */
/*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
     */
const Navigation: React.FC<Props> = ({
  appBarClass,
  setSidebarContent,
  findFunction,
  pageTitle,
}) => {
  const classes = useStyles();

  return (
    <AppBar position='static' className={appBarClass}>
      <Toolbar variant='dense'>
        <Typography variant='h6' className={classes.title}>
          <FavoriteIcon
            alignmentBaseline={'central'}
            style={{ verticalAlign: 'top', paddingTop: '7px', paddingRight: '3px' }}
          />
          <Button component={Link} to={``}>
            Nivio
          </Button>
        </Typography>
        <Box className={classes.pageTitle}>
          <Typography variant='h6'>{pageTitle}</Typography>
        </Box>
        <div className={classes.grow} />
        <Search findItem={findFunction} setSidebarContent={setSidebarContent} />
        <IconButton
          data-testid='ManualButton'
          component={Link}
          to={`/man/install.html`}
          title={'Help / Manual'}
        >
          <HelpOutlineRounded />
        </IconButton>
      </Toolbar>
    </AppBar>
  );
};

export default Navigation;
