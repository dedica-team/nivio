import React, { ReactElement } from 'react';

import GenericModal from '../../ModalComponent/GenericModal';
import { Link } from 'react-router-dom';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import { Button } from '@material-ui/core';
import './LandscapeOverview.scss';
import { ILandscape } from '../../../interfaces';

interface Props {
  modalContent: string | ReactElement | ReactElement[] | null;
  landscapes: ILandscape[] | undefined;
  enterLog: (landscape: ILandscape) => void;
}

/**
 * Displays all available landscapes and provides all needed navigation
 */

const HomeLayout: React.FC<Props> = ({ modalContent, landscapes, enterLog }) => {
  // Render
  /*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
     */
  let content: string | ReactElement[] = 'Loading landscapes...';
  if (landscapes) {
    content = landscapes.map((landscape) => {
      return (
        <Grid key={landscape.identifier} className={'landscapeContainer'} container spacing={3}>
          <Grid item xs={12} sm={12}>
            <Grid container className={'bar'} spacing={2}>
              <Grid item xs={1} sm={1} className={'first item'}></Grid>
              <Grid item xs={11} sm={'auto'} className={'title'}>
                {landscape.name}
              </Grid>
              <Grid item xs={12} sm={8} className={'item'}></Grid>
            </Grid>
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
            <br />
            {landscape.teams ? 'Teams: ' + landscape.teams.join(', ') : ''}
            <br />
          </Grid>

          <Grid item xs={12} md={3} lg={2}>
            <Typography variant='overline' display='block' gutterBottom>
              State
            </Typography>
            {landscape.overallState || '-'}
          </Grid>

          <Grid item xs={12} md={3} lg={2}>
            <Typography variant='overline' display='block' gutterBottom>
              Items
            </Typography>
            <Typography variant='h2' display='block' gutterBottom>
              {landscape.items?.length || 0}
            </Typography>
            in {landscape.groups?.length || 0} groups
          </Grid>

          <Grid item xs={12} lg={2}>
            <Typography variant='overline' display='block' gutterBottom>
              Last update
            </Typography>
            <Typography variant='h3' display='block'>
              {landscape.lastUpdate?.split(' ')[0] || '-'}
            </Typography>

            <div>{landscape.lastUpdate?.split(' ')[1] || '-'}</div>
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
              onClick={() => enterLog(landscape)}
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
      <GenericModal modalContent={modalContent} />
      {content}
    </div>
  );
};

export default HomeLayout;
