import React, { ReactElement } from 'react';

import TitleBar from '../../TitleBarComponent/TitleBar';
import { Link } from 'react-router-dom';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import { Button } from '@material-ui/core';
import './LandscapeOverview.scss';
import { ILandscape } from '../../../interfaces';
import dateFormat from 'dateformat';
import { CSSTransition } from 'react-transition-group';

interface Props {
  sliderContent: string | ReactElement | ReactElement[] | null;
  landscapes: ILandscape[] | null | undefined;
  showSlider: boolean;
  cssAnimationKey: string;
  enterLog: (e: any, landscape: ILandscape) => void;
}

/**
 * Displays all available landscapes and provides all needed navigation
 */

const LandscapeOverviewLayout: React.FC<Props> = ({
  sliderContent,
  landscapes,
  enterLog,
  showSlider,
  cssAnimationKey,
}) => {
  // Render
  /*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
     */
  let content: string | ReactElement[] = 'Loading landscapes...';
  if (Array.isArray(landscapes)) {
    content = landscapes.map((landscape) => {
      return (
        <Grid key={landscape.identifier} className={'landscapeContainer'} container spacing={3}>
          <Grid item xs={12} sm={12}>
            <TitleBar title={landscape.name} />
          </Grid>

          <Grid item xs={12} md={3} lg={2} className={'previewItem'}>
            <Button component={Link} to={`/landscape/${landscape.identifier}`}>
              <img
                className={'preview'}
                alt={'preview'}
                src={
                  process.env.REACT_APP_BACKEND_URL +
                  '/render/' +
                  landscape.identifier +
                  '/graph.png'
                }
                style={{ maxWidth: 100, float: 'left' }}
              />
            </Button>
          </Grid>
          <Grid item xs={12} md={3} lg={2}>
            <Typography variant='overline' display='block' gutterBottom>
              Info
            </Typography>
            {landscape.description}
            <br />
            <br />
            Identifier: {landscape.identifier}
            <br />
            {landscape.teams ? `Teams: ${landscape.teams.join(', ')}` : ''}
            <br />
          </Grid>

          <Grid item xs={12} md={3} lg={2}>
            <Typography variant='overline' display='block' gutterBottom>
              Items
            </Typography>
            <Typography variant='h2' display='block' gutterBottom>
              {landscape.items ? Object.keys(landscape.items).length : 0}
            </Typography>
            in {landscape.groups ? Object.keys(landscape.groups).length : 0} groups
          </Grid>

          <Grid item xs={12} lg={2}>
            <Typography variant='overline' display='block' gutterBottom>
              Last update
            </Typography>
            <Typography variant='h5' display='block'>
              {landscape.lastUpdate ? dateFormat(landscape.lastUpdate, 'dd-mm-yyyy hh:MM:ss') : '-'}
            </Typography>
          </Grid>

          <Grid item xs={12} lg={2}>
            <Button
              fullWidth
              component={Link}
              className={'button stackedButton'}
              to={`/landscape/${landscape.identifier}`}
            >
              enter
            </Button>

            <Button
              onClick={(e) => enterLog(e, landscape)}
              fullWidth
              className={'button stackedButton'}
            >
              log
            </Button>

            <Button
              fullWidth
              className={'button stackedButton'}
              rel='noopener noreferrer'
              target={'_blank'}
              href={
                process.env.REACT_APP_BACKEND_URL + '/render/' + landscape.identifier + '/map.svg'
              }
            >
              Printable Graph
            </Button>

            <Button
              fullWidth
              className={'button'}
              rel='noopener noreferrer'
              target={'_blank'}
              href={
                process.env.REACT_APP_BACKEND_URL + '/docs/' + landscape.identifier + '/report.html'
              }
            >
              Printable Report
            </Button>
          </Grid>
        </Grid>
      );
    });
  }

  return (
    <div className='homeContainer'>
      <CSSTransition
        key={cssAnimationKey}
        in={showSlider}
        timeout={{ enter: 0, exit: 1000, appear: 1000 }}
        appear
        unmountOnExit
        classNames='logContent'
      >
        <React.Fragment>{sliderContent}</React.Fragment>
      </CSSTransition>
      {content}
    </div>
  );
};

export default LandscapeOverviewLayout;
