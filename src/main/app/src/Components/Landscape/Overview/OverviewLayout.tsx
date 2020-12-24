import React, { ReactElement } from 'react';

import { Link } from 'react-router-dom';
import Grid from '@material-ui/core/Grid';
import { Box, Button, Card, CardActions, CardHeader, CardMedia, Theme } from '@material-ui/core';
import { ILandscape } from '../../../interfaces';
import dateFormat from 'dateformat';
import { withBasePath } from '../../../utils/API/BasePath';
import IconButton from '@material-ui/core/IconButton';
import { Assignment, FormatListBulleted, MapOutlined } from '@material-ui/icons';
import Log from '../Modals/Log/Log';
import CardContent from '@material-ui/core/CardContent';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      marginBottom: 5,
      backgroundColor: theme.palette.secondary.dark,
      height: '100%',
      color: 'white',
    },
    cardHeader: {
      backgroundColor: theme.palette.secondary.main,
      padding: 10,
    },
    cardSubheader: {
      fontSize: '0.8rem',
    },
    cardActions: {
      padding: 0,
    },
    link: {
      display: 'block',
      borderRadius: 5,
      backgroundColor: 'black',
    },
    cardMedia: {
      height: 140,
      maxWidth: '100%',
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
            className={classes.cardHeader}
            classes={{ subheader: classes.cardSubheader }}
          />
          <CardContent>
            <Button
              component={Link}
              to={`/landscape/${landscape.identifier}`}
              className={classes.link}
            >
              <CardMedia
                className={classes.cardMedia}
                image={withBasePath(`/render/${landscape.identifier}/map.svg`)}
              />
            </Button>
            <br />
            {landscape.description}
          </CardContent>
          <CardActions className={classes.cardActions}>
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
              <Assignment />
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
