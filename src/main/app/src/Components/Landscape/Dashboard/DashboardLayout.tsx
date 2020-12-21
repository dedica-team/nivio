import React from 'react';

import Grid from '@material-ui/core/Grid';
import { ILandscape, IAssessment, IGroup, IItem } from '../../../interfaces';
import { getAssessmentSummary, getItemIcon } from '../Utils/utils';
import { Card, Paper, Theme, Typography } from '@material-ui/core';
import StatusChip from '../../StatusChip/StatusChip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    item: {
      width: '7rem',
      height: '5rem',
      overflow: 'hidden',
      margin: 5,
      padding: 5,
      textAlign: 'center',
      backgroundColor: 'transparent',
      color: theme.palette.secondary.main,
    },
    icon: {
      margin: 'auto',
      backgroundColor: 'white',
      border: '2px solid',
    },
    groupCard: {
      backgroundColor: theme.palette.secondary.main,
    },
  })
);

interface Props {
  landscape: ILandscape | null | undefined;
  assessments: IAssessment | undefined;
  onItemClick: (item: IItem) => void;
  onGroupClick: (fullyQualifiedGroupIdentifier: string) => void;
  onGroupAssessmentClick: (fullyQualifiedGroupIdentifier: string) => void;
  onItemAssessmentClick: (fullyQualifiedItemIdentifier: string) => void;
  findItem?: Function;
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
}) => {
  // Render
  /*
                                    value         |0px     600px    960px    1280px   1920px
                                    key           |xs      sm       md       lg       xl
                                    screen width  |--------|--------|--------|--------|-------->
                                    range         |   xs   |   sm   |   md   |   lg   |   xl
                                     */

  const classes = useStyles();
  const getItems = (group: IGroup) => {
    const groupColor = `#${group.color}` || 'grey';
    return group.items.map((item) => {
      const [assessmentColor, , assessmentField] = getAssessmentSummary(
        assessments?.results[item.fullyQualifiedIdentifier]
      );

      return (
        <Paper
          variant={'outlined'}
          className={classes.item}
          key={item.fullyQualifiedIdentifier}
          onClick={() => onItemClick(item)}
          style={{ borderColor: assessmentColor }}
        >
          <Avatar
            variant={'circle'}
            src={getItemIcon(item)}
            className={classes.icon}
            style={{ borderColor: groupColor }}
          />
          {item.name || item.identifier}
          {assessmentField.length > 0 ? (
            <StatusChip name={assessmentField} status={assessmentColor} />
          ) : (
            ''
          )}
        </Paper>
      );
    });
  };

  const getGroupsCard = (groups: IGroup[]) => {
    if (!groups) return;

    const elements = groups.map((group) => {
      if (group.items.length > 0) {
        const groupColor = `#${group.color}` || 'grey';
        const [groupAssessmentColor, , groupAssessmentField] = getAssessmentSummary(
          assessments?.results[group.fullyQualifiedIdentifier]
        );

        return (
          <Paper
            key={group.name}
            className={classes.item}
            id={group.fullyQualifiedIdentifier}
            onClick={() => onGroupClick(group.fullyQualifiedIdentifier)}
            style={{ backgroundColor: groupColor }}
          >
            <Typography variant={'h6'}>{group.name}</Typography>
            <span
              className='smallDot'
              id={group.fullyQualifiedIdentifier}
              style={{ backgroundColor: groupAssessmentColor }}
              onClick={() => onGroupAssessmentClick(group.fullyQualifiedIdentifier)}
            />
            <StatusChip name={groupAssessmentField} status={groupAssessmentColor} />
          </Paper>
        );
      }

      return null;
    });

    return (
      <Card className={classes.groupCard}>
        <Grid container spacing={0}>
          {elements}
        </Grid>
      </Card>
    );
  };

  if (!landscape) return null;
  return (
    <Grid container spacing={3}>
      <Grid item xs={12} style={{ padding: 5 }}>
        {getGroupsCard(landscape.groups)}
      </Grid>
      {landscape?.groups.map((group, i) => getItems(group))}
    </Grid>
  );
};

export default DashboardLayout;
