import React from 'react';
import Grid from '@material-ui/core/Grid';
import {Button, Typography, AppBar, Theme, createStyles} from '@material-ui/core';
import {Link} from 'react-router-dom';

import Toolbar from '@material-ui/core/Toolbar';
import './Navigation.scss';
import Search from "../SearchComponent/Search";
import AcUnitIcon from '@material-ui/icons/AcUnit';

import makeStyles from "@material-ui/core/styles/makeStyles";

const useStyles = makeStyles((theme: Theme) =>
    createStyles({
        grow: {
            flexGrow: 1,
        },
        title: {
            marginRight: '20px'
        }
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

    return <AppBar position="static" className={'appBar'}>
        <Toolbar>
            <Typography variant="h6" className={classes.title}>
                <AcUnitIcon alignmentBaseline={"central"}
                            style={{verticalAlign: 'top', paddingTop: '3px', paddingRight: '3px'}}/>Nivio
            </Typography>
            <Button component={Link} to={``}>
                Home
            </Button>
            <Button
                data-testid='ManualButton'
                component={Link}
                to={`/man/install`}
            >
                Manual
            </Button>
            <Button
                data-testid='EventsButton'
                component={Link}
                to={`/events`}
            >
                Events
            </Button>
            <div className={classes.grow}/>
            <Search/>
        </Toolbar>
    </AppBar>;

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
                    <Search/>
                </Grid>
            </Grid>
        </div>
    );
};

export default Navigation;
