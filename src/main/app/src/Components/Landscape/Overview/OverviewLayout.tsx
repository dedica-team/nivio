import React, { ReactElement } from 'react';

import { Link } from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import { Box, Button, Card, CardHeader, CardMedia, darken, Theme } from '@material-ui/core';
import { ILandscape } from '../../../interfaces';
import dateFormat from 'dateformat';
import { withBasePath } from '../../../utils/API/BasePath';
import IconButton from '@material-ui/core/IconButton';
import { Assignment, FormatListBulleted, MapOutlined } from '@material-ui/icons';
import Log from '../Modals/Log/Log';
import CardContent from '@material-ui/core/CardContent';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import componentStyles from '../../../Resources/styling/ComponentStyles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    overview: {
      marginLeft: 10,
      width: 'calc(100% - 300px)',
      paddingTop: 0
    },
    card: {
      marginBottom: 5,
      backgroundColor: darken(theme.palette.primary.main, 0.3),
      height: '100%',
      color: 'white',
    },
    link: {
      display: 'block',
      borderRadius: 5,
      backgroundColor: 'black',
    },
    cardMedia: {
      height: 200,
      maxWidth: '100%',
      backgroundSize: 'contain',
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
  const classes = useStyles();
  const componentClasses = componentStyles();
  let content: ReactElement[] = [<Box>Loading landscapes...</Box>];

  if (Array.isArray(landscapes) && landscapes.length) {

    content = landscapes.map((landscape) => {
      let itemCount = 0;
      landscape.groups?.forEach((group) => (itemCount += group.items.length));
      let stats =
        itemCount +
        ' items in ' +
        (landscape.groups ? Object.keys(landscape.groups).length : 0) +
        ' groups';

      if (landscape.lastUpdate)
        stats += ', updated: ' + dateFormat(landscape.lastUpdate, 'dd-mm-yyyy hh:MM:ss TT');

      return (
        <Card key={landscape.identifier} className={classes.card}>
          <CardHeader
            title={landscape.name}
            subheader={stats}
            className={componentClasses.cardHeader}
            classes={{ subheader: componentClasses.cardSubheader }}
            action={
              <React.Fragment>
                <IconButton
                  aria-label='log'
                  title={'process log'}
                  onClick={() => setSidebarContent(<Log landscape={landscape} />)}
                >
                  <FormatListBulleted />
                </IconButton>
                <IconButton
                  aria-label='map'
                  title={'SVG Export'}
                  rel='noopener noreferrer'
                  target={'_blank'}
                  href={withBasePath(`/render/${landscape.identifier}/map.svg`)}
                >
                  <MapOutlined />
                </IconButton>
                <IconButton
                  aria-label='report'
                  title={'Printable Report'}
                  rel='noopener noreferrer'
                  target={'_blank'}
                  href={withBasePath(`/docs/${landscape.identifier}/report.html`)}
                >
                  <Assignment />
                </IconButton>
              </React.Fragment>
            }
          />
          <CardContent>
            <Button
              aria-label={'map'}
              component={Link}
              to={`/landscape/${landscape.identifier}`}
              className={classes.link}
              title={'Landscape map'}
            >
              <CardMedia
                className={classes.cardMedia}
                image={withBasePath(`/render/${landscape.identifier}/map.svg`)}
              />
            </Button>
            <br />
            {landscape.description}
          </CardContent>
        </Card>
      );
    });

  }

  return (
    <Grid container spacing={3} className={classes.overview}>
      {content.map((value, i) => (
        <Grid item xs={12} sm={6} key={i}>
          {value}
        </Grid>
      ))}
    </Grid>
  );
};
export default OverviewLayout;
