import React from 'react';
import Grid from '@material-ui/core/Grid';
import './TitleBar.scss';

interface Props {
  title: string;
}

/*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
  */
const TitleBar: React.FC<Props> = ({ title }) => {
  return (
    <Grid container className={'titleBar'} spacing={0}>
      <Grid item xs={1} className={'first'}></Grid>
      <Grid item xs={10} sm={8} md={6} lg={4} xl={3} className={'title'}>
        {title}
      </Grid>
      <Grid item xs={1} sm={3} md={5} lg={7} xl={8} className={'last'}></Grid>
    </Grid>
  );
};

export default TitleBar;
