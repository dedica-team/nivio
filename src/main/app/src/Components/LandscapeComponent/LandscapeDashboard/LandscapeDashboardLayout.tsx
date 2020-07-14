import React, { ReactElement, MouseEvent } from 'react';

import Grid from '@material-ui/core/Grid';
import './LandscapeDashboard.scss';
import { ILandscape, IItem, IAssessment } from '../../../interfaces';
import { getAssessmentColorAndMessage } from '../../../utils/styling/style-helper';

interface Props {
  landscape: ILandscape | null | undefined;
  assessments: IAssessment | null;
  onItemClick: (e: MouseEvent<HTMLSpanElement>, item: IItem) => void;
}

const defaultColor = 'grey';

/**
 * Displays all groups of given landscape and provides all needed navigation
 */

const LandscapeDashboardLayout: React.FC<Props> = ({ landscape, assessments, onItemClick }) => {
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
        let assesmentMessage = '';
        if (assessments) {
          const assesmentResults = assessments.results[item.fullyQualifiedIdentifier];
          [assessmentColor, assesmentMessage] = getAssessmentColorAndMessage(
            assesmentResults,
            item.identifier
          );
        }
        return (
          <Grid item key={item.identifier} className={'itemContainer'}>
            <span className='dot' style={{ backgroundColor: assessmentColor }}>
              <span
                className='statusDot'
                onClick={(e: MouseEvent<HTMLSpanElement>) => onItemClick(e, item)}
              >
                <span className='statusField'>{assesmentMessage}</span>
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

export default LandscapeDashboardLayout;
