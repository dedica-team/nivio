import React from 'react';

import Grid from '@material-ui/core/Grid';
import { IAssessment, IGroup, ILandscape } from '../../../interfaces';
import { getAssessmentSummary } from '../Utils/utils';
import StatusChip from '../../StatusChip/StatusChip';
import Button from '@material-ui/core/Button';
import Toolbar from '@material-ui/core/Toolbar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import {darken, Theme} from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    bottomBar: {
      top: 'auto',
      bottom: 0,
      left: 0,
      padding: 5,
      position: 'fixed',
      width: '100%',
      backgroundColor: darken(theme.palette.secondary.dark, 0.2),
    },
  })
);

interface Props {
  landscape: ILandscape;
  assessments: IAssessment;
  onItemClick: Function;
  onGroupClick: Function;
}

/**
 * Displays all groups of given landscape and provides all needed navigation
 */
const StatusBarLayout: React.FC<Props> = ({
  landscape,
  assessments,
  onItemClick,
  onGroupClick,
}) => {
  const classes = useStyles();
  const getItems = (group: IGroup) => {
    return group.items.map((item) => {
      const [assessmentColor, , field] = getAssessmentSummary(
        assessments?.results[item.fullyQualifiedIdentifier]
      );

      if (field === '') return null;

      return (
        <Button key={item.fullyQualifiedIdentifier} onClick={() => onItemClick(item)}>
          <StatusChip
            name={(item.name || item.identifier) + ' ' + field}
            status={assessmentColor}
          />
        </Button>
      );
    });
  };

  const getGroups = (groups: IGroup[]) => {
    if (!groups) return;

    return groups.map((group) => {
      if (group.items.length === 0) {
        console.debug('Skipping group without items');
        return null;
      }

      const groupColor = `#${group.color}` || 'grey';
      const [groupAssessmentColor, , groupAssessmentField] = getAssessmentSummary(
        assessments?.results[group.fullyQualifiedIdentifier]
      );

      if (groupAssessmentField === '') {
        console.debug('Group ' + group.fullyQualifiedIdentifier + ' has no summary assessment');
        return null;
      }

      const title = 'Group ' + group.name;
      return (
        <Button
          id={group.fullyQualifiedIdentifier}
          onClick={() => onGroupClick(group)}
          key={group.name}
        >
          <StatusChip
            name={title}
            status={groupAssessmentColor}
            style={{
              backgroundColor: groupColor,
            }}
          />
        </Button>
      );
    });
  };

  return (
    <Toolbar className={classes.bottomBar} variant={'dense'} disableGutters={true}>
      <Grid container spacing={0}>
        {getGroups(landscape.groups)}
        {landscape?.groups.map((group, i) => getItems(group))}
      </Grid>
    </Toolbar>
  );
};

export default StatusBarLayout;
