import React from 'react';
import Grid from "@material-ui/core/Grid";
import './TitleBar.scss';

const TitleBar: React.FC<any> = ({title}) => {

    return <Grid container className={'bar'} spacing={2}>
        <Grid item xs={1} sm={1} className={'first item'}></Grid>
        <Grid item xs={11} sm={'auto'} className={'title'}>
            {title}
        </Grid>
        <Grid item xs={12} sm={8} className={'item'}></Grid>
    </Grid>
}

export default TitleBar;