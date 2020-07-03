import React, { ReactElement } from 'react';

import TitleBar from '../../TitleBarComponent/TitleBar';
import Typography from '@material-ui/core/Typography';
import Grid from '@material-ui/core/Grid';
import './LandscapeDashboard.scss';
import { ILandscape } from '../../../interfaces';

interface Props {
  landscape: ILandscape | null | undefined;
}

/**
 * Displays all groups of given landscape and provides all needed navigation
 */

const LandscapeDashboardLayout: React.FC<Props> = ({ landscape }) => {
  // Render
  /*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
     */
  let content: string | ReactElement[] = 'Loading landscapes...';
  if (landscape && landscape.groups) {
    content = landscape.groups.map((group) => {
      const items: ReactElement[] = group.items.map((item) => {
        return (
          <Grid key={item.identifier} className={'itemContainer'} container spacing={3}>
            <Grid item xs={12} md={2} className={'itemIcon'}>
              <img src={item.labels?.['nivio.rendered.icon']} className='icon' alt={'icon'} />
            </Grid>
            <Grid item xs={12} md={3} lg={2} className={'itemName'}>
              <Typography variant='overline' display='block' gutterBottom>
                Name
              </Typography>
              {item.name || item.identifier}
            </Grid>
            <Grid item xs={12} md={3} lg={4} className={'itemDescription'}>
              <Typography variant='overline' display='block' gutterBottom>
                Description
              </Typography>
              {item.description || 'No description provided'}
            </Grid>
            <Grid item xs={12} md={3} lg={2} className={'itemContact'}>
              <Typography variant='overline' display='block' gutterBottom>
                Contact
              </Typography>
              {item.contact || item.owner || 'No contact provided'}
            </Grid>
            <Grid item xs={12} md={3} lg={2} className={'itemContact'}>
              <Typography variant='overline' display='block' gutterBottom>
                Owner
              </Typography>
              {item.owner || 'No owner provided'}
            </Grid>
          </Grid>
        );
      });
      return (
        <Grid key={group.name} className={'groupContainer'} container spacing={3}>
          <Grid item xs={12} sm={12}>
            <TitleBar title={group.name} />
          </Grid>
          {items}
        </Grid>
      );
    });
  }

  return (
    <div className='landscapeDashboardContainer'>
      <span className='title'>{landscape ? `${landscape.name}` : null}</span>
      {content}
    </div>
  );
};

export default LandscapeDashboardLayout;
