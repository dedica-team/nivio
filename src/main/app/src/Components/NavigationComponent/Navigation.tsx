import React from 'react';
import Grid from '@material-ui/core/Grid';
import { Button } from '@material-ui/core';
import { Link } from 'react-router-dom';

import './Navigation.scss';
import Search from "../SearchComponent/Search";

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
  return (
    <div className='navigationContainer'>
      <Grid container spacing={1} className={'header'}>
        <Grid item xs={12} sm={1} md={1} xl={1} className={'first'}></Grid>
        <Grid item xs={4} sm={2} md={2} lg={1} xl={1} className='buttonContainer'>
          <Button component={Link} to={``} fullWidth className={'navButton firstButton'}>
            Home
          </Button>
        </Grid>
        <Grid item xs={4} sm={2} md={2} lg={1} xl={1} className='buttonContainer'>
          <Button
            data-testid='ManualButton'
            component={Link}
            to={`/man/install`}
            fullWidth
            className={'navButton'}
          >
            Manual
          </Button>
        </Grid>

        <Grid item xs={4} sm={2} md={2} lg={1} xl={1} className='buttonContainer'>
          <Button
            data-testid='EventsButton'
            component={Link}
            to={`/events`}
            fullWidth
            className={'navButton'}
          >
            Events
          </Button>
        </Grid>
        <Grid item className={'last'} xs={12} sm={1} md={1} lg={2} xl={2}></Grid>
        <Grid item className={'title'} xs={12} sm={3} md={3} lg={3} xl={3}>
          Nivio
        </Grid>
        <Grid item className={'last'} xs={12} sm={1} md={1} lg={3} xl={3}>
          <Search />
        </Grid>
      </Grid>
    </div>
  );
};

export default Navigation;
