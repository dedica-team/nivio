import React, { ReactElement, MouseEvent } from 'react';

import Grid from '@material-ui/core/Grid';
import './LandscapeDashboard.scss';
import { ILandscape, IItem, IAssesment, IAssesmentProps } from '../../../interfaces';

interface Props {
  landscape: ILandscape | null | undefined;
  assesments: IAssesment | null;
  onItemClick: (e: MouseEvent<HTMLSpanElement>, item: IItem) => void;
}

const defaultColor = '#008000';

/**
 * Displays all groups of given landscape and provides all needed navigation
 */

const LandscapeDashboardLayout: React.FC<Props> = ({ landscape, assesments, onItemClick }) => {
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
      const groupColor = `#${group.color}` || defaultColor;
      const items: ReactElement[] = group.items.map((item) => {
        let assessmentColor = defaultColor;
        let assessmentField = '';
        if (assesments) {
          if (assesments.results[item.fullyQualifiedIdentifier]) {
            const itemResults = assesments.results[item.fullyQualifiedIdentifier];
            [assessmentColor, assessmentField] = getAssesmentColorAndField(itemResults);
          }
        }
        return (
          <Grid item key={item.identifier} className={'itemContainer'}>
            <span className='dot' style={{ backgroundColor: assessmentColor }}>
              <span
                className='statusDot'
                onClick={(e: MouseEvent<HTMLSpanElement>) => onItemClick(e, item)}
              >
                <span className='statusField'>{assessmentField}</span>
              </span>
            </span>
            <div className='itemDescription'>
              <img src={item.labels?.['nivio.rendered.icon']} className='icon' alt={'icon'} />
              <span className='itemName'>{item.name || item.identifier}</span>
            </div>
          </Grid>
        );
      });
      return (
        <Grid item key={group.name} className='group'>
          <Grid item className='groupName' style={{ backgroundColor: groupColor }}>
            <span>{group.name || group.identifier || ''}</span>
          </Grid>
          <Grid item className={'items'}>
            {items}
          </Grid>
        </Grid>
      );
    });
  }

  return (
    <div className='landscapeDashboardContainer'>
      <div className='title'>
        <span>{landscape ? `${landscape.name}` : null}</span>
      </div>
      <Grid key={'group'} className={'groupContainer'} container spacing={5}>
        {content}
      </Grid>
    </div>
  );
};

const getAssesmentColorAndField = (assesmentResults: IAssesmentProps[]): string[] => {
  let assesmentColor = defaultColor;
  let assesmentField = '';

  const result = assesmentResults.find(
    (assesmentResult) => assesmentResult.field === 'summary.kpi-dashboard'
  );

  if (result) {
    if (result.status !== 'UNKNOWN') {
      assesmentColor = result.status;
    }
    assesmentField = result.status;
  }

  return [assesmentColor, assesmentField];
};

export default LandscapeDashboardLayout;
