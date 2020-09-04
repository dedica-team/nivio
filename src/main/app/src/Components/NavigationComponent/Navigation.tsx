import React from 'react';
// import Grid from '@material-ui/core/Grid';
import { Button, Typography, AppBar, Theme, createStyles, ButtonGroup } from '@material-ui/core';
import { Link } from 'react-router-dom';

import Toolbar from '@material-ui/core/Toolbar';
import './Navigation.scss';

import FavoriteIcon from '@material-ui/icons/Favorite';

import makeStyles from '@material-ui/core/styles/makeStyles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    grow: {
      flexGrow: 1,
    },
    title: {
      marginRight: '20px',
      color: 'rgba(255, 255, 255, 0.75)',
    },
  })
);

/**
 * Header Component
 */
/*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
     */
const Navigation: React.FC = () => {
  const classes = useStyles();

  return (
    <AppBar position='static' className={'appBar'}>
      <Toolbar className={'toolBar'}>
        <Typography variant='h6' className={classes.title}>
          <FavoriteIcon
            alignmentBaseline={'central'}
            style={{ verticalAlign: 'top', paddingTop: '7px', paddingRight: '3px' }}
          />
          Nivio
        </Typography>
        <ButtonGroup variant={'text'} aria-label='contained primary button group'>
          <Button component={Link} to={``}>
            Home
          </Button>
          <Button data-testid='ManualButton' component={Link} to={`/man/install`}>
            Manual
          </Button>
          <Button data-testid='EventsButton' component={Link} to={`/events`}>
            Events
          </Button>
        </ButtonGroup>
        <div className={classes.grow} />
      </Toolbar>
    </AppBar>
  );
};

export default Navigation;
