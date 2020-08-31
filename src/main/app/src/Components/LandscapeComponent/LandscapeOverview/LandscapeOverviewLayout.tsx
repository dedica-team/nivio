import React, { ReactElement, MouseEvent } from 'react';

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
  enterLog: (e: MouseEvent<HTMLButtonElement>, landscape: ILandscape) => void;
}

/**
 * Displays all available landscapes and provides all needed navigation
 */

const LandscapeOverviewLayout: React.FC<Props> = ({
  sliderContent,
  landscapes,
  enterLog,
  showSlider,
}) => {
  // Render
  /*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
  */
  let content: string | ReactElement[] = 'Loading landscapes...';
  const backendUrl = process.env.REACT_APP_BACKEND_URL || '';

  if (Array.isArray(landscapes) && landscapes.length) {
    content = landscapes.map((landscape) => {
      let itemCount = 0;
      landscape.groups?.forEach((group) => (itemCount += group.items.length));
      return (
        <Grid key={landscape.identifier} className={'landscapeContainer'} container spacing={3}>
          <Grid item xs={12}>
            <TitleBar title={landscape.name} />
          </Grid>

          <Grid item xs={12} sm={4} md={2} lg={2} xl={1} className={'previewItem'}>
            <Button component={Link} to={`/landscape/${landscape.identifier}`}>
              <img
                className={'preview'}
                alt={'preview'}
                src={`${backendUrl}/render/${landscape.identifier}/graph.png`}
                style={{ maxWidth: 100, float: 'left' }}
              />
            </Button>
          </Grid>
          <Grid item xs={12} sm={6} md={3} lg={3} xl={3} className='infoContainer'>
            <Typography variant='overline' display='block' gutterBottom>
              Info
            </Typography>
            <div className='infoContent'>
              <span className='description'>{landscape.description}</span>
              <span className='identifier'>Identifier: {landscape.identifier}</span>
            </div>
          </Grid>

          <Grid item xs={12} sm={4} md={1} lg={1} xl={1} className='itemContainer'>
            <Typography variant='overline' display='block' className='itemTitle'>
              Items
            </Typography>
            <Typography variant='h2' display='block' className='itemCount'>
              {itemCount}
            </Typography>
            <span className='itemGroups'>
              in {landscape.groups ? Object.keys(landscape.groups).length : 0} groups
            </span>
          </Grid>

          <Grid item xs={12} sm={6} md={3} lg={3} xl={3}>
            <Typography variant='overline' display='block' gutterBottom>
              Last update
            </Typography>
            <Typography variant='h5' display='block'>
              {landscape.lastUpdate
                ? dateFormat(landscape.lastUpdate, 'dd-mm-yyyy hh:MM:ss TT')
                : '-'}
            </Typography>
          </Grid>

          <Grid item xs={12} sm={6} md={3} lg={3} xl={4} className='last'>
            <Button variant="outlined" color="primary"
              fullWidth
              component={Link}
              to={`/landscape/${landscape.identifier}/dashboard`}
            >
              enter
            </Button>

            <Button variant="outlined" color="secondary"
                    onClick={(e) => enterLog(e, landscape)} fullWidth
            >
              log
            </Button>

            <Button
              fullWidth
              className={'noShadow'}
              color="secondary"
              variant="outlined"
              rel='noopener noreferrer'
              target={'_blank'}
              href={`${backendUrl}/render/${landscape.identifier}/map.svg`}
            >
              Printable Graph
            </Button>

            <Button
              fullWidth
              className={'noShadow'}
              color="secondary"
              variant="outlined"
              rel='noopener noreferrer'
              target={'_blank'}
              href={`${backendUrl}/docs/${landscape.identifier}/report.html`}
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
        in={showSlider}
        timeout={{ enter: 0, exit: 1000, appear: 1000 }}
        appear
        unmountOnExit
        classNames='slider'
      >
        <React.Fragment>{sliderContent}</React.Fragment>
      </CSSTransition>
      {content}
    </div>
  );
};

export default LandscapeOverviewLayout;
