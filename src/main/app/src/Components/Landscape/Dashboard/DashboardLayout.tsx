import React from 'react';

import Grid from '@material-ui/core/Grid';
import './Dashboard.scss';
import { ILandscape, IAssessment, IGroup } from '../../../interfaces';
import { getAssessmentSummary } from '../Utils/utils';

import Search from '../Search/Search';

interface Props {
  landscape: ILandscape | null | undefined;
  assessments: IAssessment | undefined;
  onItemClick: (fullyQualifiedItemIdentifier: string) => void;
  onGroupClick: (fullyQualifiedGroupIdentifier: string) => void;
  onGroupAssessmentClick: (fullyQualifiedGroupIdentifier: string) => void;
  onItemAssessmentClick: (fullyQualifiedItemIdentifier: string) => void;
  findItem: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Displays all groups of given landscape and provides all needed navigation
 */

const DashboardLayout: React.FC<Props> = ({
  landscape,
  assessments,
  onItemClick,
  onGroupClick,
  onGroupAssessmentClick,
  onItemAssessmentClick,
  findItem,
}) => {
  // Render
  /*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
     */

  const getItems = (group: IGroup) => {
    return group.items.map((item) => {
      const [assessmentColor, , assessmentField] = getAssessmentSummary(
        assessments?.results[item.fullyQualifiedIdentifier]
      );

      return (
        <Grid item key={item.identifier} className={'itemContainer'}>
          <span
            className='dot'
            id={item.fullyQualifiedIdentifier}
            style={{ backgroundColor: assessmentColor }}
          >
            <span
              className='statusDot'
              onClick={() => onItemAssessmentClick(item.fullyQualifiedIdentifier)}
            >
              <span className='statusField'>{assessmentField}</span>
            </span>
          </span>
          <div className='itemDescription'>
            <img src={item?.icon} className='icon' alt={'icon'} />
            <span className='itemName' onClick={() => onItemClick(item.fullyQualifiedIdentifier)}>
              {item.name || item.identifier}
            </span>
          </div>
        </Grid>
      );
    });
  };

  const getDashboardContent = () => {
    if (landscape && landscape.groups) {
      return landscape.groups.map((group) => {
        if (group.items.length > 0) {
          const groupColor = `#${group.color}` || 'grey';
          const [groupAssessmentColor] = getAssessmentSummary(
            assessments?.results[group.fullyQualifiedIdentifier]
          );

          const items = getItems(group);

          return (
            <Grid item key={group.name} className='group' id={group.fullyQualifiedIdentifier}>
              <Grid item className='groupName' style={{ backgroundColor: groupColor }}>
                <span
                  className='groupLabel'
                  onClick={() => onGroupClick(group.fullyQualifiedIdentifier)}
                >
                  {group.name || group.identifier || ''}
                </span>
                <span
                  className='smallDot'
                  id={group.fullyQualifiedIdentifier}
                  style={{ backgroundColor: groupAssessmentColor }}
                  onClick={() => onGroupAssessmentClick(group.fullyQualifiedIdentifier)}
                ></span>
              </Grid>
              <Grid item className={'items'}>
                {items}
              </Grid>
            </Grid>
          );
        }
        return <React.Fragment key={group.identifier} />;
      });
    }
    return 'Loading landscapes...';
  };

  return (
    <div className='landscapeDashboardContainer' id='landscapeDashboardContainer'>
      <Grid className={'titleContainer'} container spacing={2}>
        <Grid item className='title' xs={12} md={7} lg={8} xl={10}>
          {landscape ? `${landscape.name}` : null}
        </Grid>
        <Grid item xs={12} md={5} lg={4} xl={2}>
          <Search findItem={findItem} />
        </Grid>
      </Grid>
      <Grid key={'group'} className={'groupContainer'} container spacing={5}>
        {getDashboardContent()}
      </Grid>
    </div>
  );
};

export default DashboardLayout;
