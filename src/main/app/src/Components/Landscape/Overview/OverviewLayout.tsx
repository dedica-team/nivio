import React, { ReactElement } from 'react';

import { Link } from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import {Box, Button, Card, CardActions, CardHeader, CardMedia, Theme} from '@material-ui/core';
import { ILandscape } from '../../../interfaces';
import dateFormat from 'dateformat';
import { withBasePath } from '../../../utils/API/BasePath';
import IconButton from '@material-ui/core/IconButton';
import { FormatListBulleted, MapOutlined, Report } from '@material-ui/icons';
import Log from '../Modals/Log/Log';
import CardContent from '@material-ui/core/CardContent';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      marginBottom: 5,
      padding: 5,
      backgroundColor: '#161618',
      borderColor: theme.palette.secondary.main,
      height: "100%",
    },
  })
);

interface Props {
  landscapes: ILandscape[] | null | undefined;
  setSidebarContent: Function;
}

/**
 * Displays all available landscapes and provides all needed navigation
 */

const OverviewLayout: React.FC<Props> = ({ landscapes, setSidebarContent }) => {
  // Render
  /*
                    value         |0px     600px    960px    1280px   1920px
                    key           |xs      sm       md       lg       xl
                    screen width  |--------|--------|--------|--------|-------->
                    range         |   xs   |   sm   |   md   |   lg   |   xl
                  */
  const classes = useStyles();
  let content: ReactElement[] = [<Box>Loading landscapes...</Box>];

  if (Array.isArray(landscapes) && landscapes.length) {
    content = landscapes.map((landscape) => {
      let itemCount = 0;
      landscape.groups?.forEach((group) => (itemCount += group.items.length));
      return (
        <Card key={landscape.identifier} className={classes.card} variant={'outlined'}>
          <CardHeader
            title={landscape.name}
            subheader={
              'Last update ' + landscape.lastUpdate
                ? dateFormat(landscape.lastUpdate, 'dd-mm-yyyy hh:MM:ss TT')
                : '-'
            }
          />
          <CardContent>
            <Button component={Link} to={`/landscape/${landscape.identifier}`}>
              <CardMedia
                  component="img"
                  alt="map"
                  height="140" width={'100%'}
                  image={withBasePath(`/render/${landscape.identifier}/map.svg`)}
              />

            </Button>
            {landscape.description}
            <br />
            <span>
              {itemCount} items in {landscape.groups ? Object.keys(landscape.groups).length : 0}{' '}
              groups
            </span>
          </CardContent>
          <CardActions>
            <Button
              variant='outlined'
              color='primary'
              fullWidth
              component={Link}
              to={`/landscape/${landscape.identifier}/dashboard`}
            >
              enter
            </Button>

            <IconButton
              aria-label='log'
              color={'secondary'}
              onClick={() => setSidebarContent(<Log landscape={landscape} />)}
            >
              <FormatListBulleted />
            </IconButton>

            <IconButton
              aria-label='map'
              color={'secondary'}
              title={'SVG Export'}
              rel='noopener noreferrer'
              target={'_blank'}
              href={withBasePath(`/render/${landscape.identifier}/map.svg`)}
            >
              <MapOutlined />
            </IconButton>

            <IconButton
              aria-label='report'
              color={'secondary'}
              title={'Printable Report'}
              rel='noopener noreferrer'
              target={'_blank'}
              href={withBasePath(`/docs/${landscape.identifier}/report.html`)}
            >
              <Report />
            </IconButton>
          </CardActions>
        </Card>
      );
    });
  }

  return (
    <Grid container spacing={3}>
      {content.map((value, i) => (
        <Grid item xs={12} sm={6} key={i}>
          {value}
        </Grid>
      ))}
    </Grid>
  );
};
export default OverviewLayout;
