import React from 'react';

import Grid from '@material-ui/core/Grid';
import { ILandscape, IAssessment, IGroup, IItem } from '../../../interfaces';
import { getAssessmentSummary } from '../Utils/utils';
import { Box, Card, CardHeader, Theme, Typography } from '@material-ui/core';
import StatusChip from '../../StatusChip/StatusChip';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    item: {
      width: '7rem',
      height: '6rem',
      overflow: 'hidden',
      margin: 5,
      textAlign: 'center',
      backgroundColor: theme.palette.secondary.dark, //'transparent',
      color: 'white',
      border: '1px solid',
    },
    itemHeader: {
      fontSize: 'small',
      color: 'black',
    },
    icon: {
      margin: 'auto',
      backgroundColor: 'white',
      border: '2px solid',
    },
    groupCard: {
      backgroundColor: 'transparent',
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
    //const groupColor = `#${group.color}` || 'grey';
    return group.items.map((item) => {
      const [assessmentColor, msg, field] = getAssessmentSummary(
        assessments?.results[item.fullyQualifiedIdentifier]
      );

      return (
        <Card
          className={classes.item}
          key={item.fullyQualifiedIdentifier}
          onClick={() => onItemClick(item)}
          style={{ borderColor: assessmentColor }}
        >
          <CardHeader
            disableTypography={true}
            className={classes.itemHeader}
            style={{ backgroundColor: assessmentColor, padding: 5, marginBottom: 10 }}
            title={item.name || item.identifier}
          />

          {field.length > 0 ? (
            <Box>
              {field}:<br />
              {msg}
            </Box>
          ) : (
            ''
          )}
        </Card>
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
          <Card
            key={group.name}
            className={classes.item}
            id={group.fullyQualifiedIdentifier}
            onClick={() => onGroupClick(group.fullyQualifiedIdentifier)}
            style={{ borderColor: groupAssessmentColor }}
          >
            <CardHeader
              disableTypography={true}
              className={classes.itemHeader}
              style={{
                backgroundColor: groupColor,
                padding: 5,
                marginBottom: 10,
                borderBottomColor: groupAssessmentColor,
              }}
              title={group.name}
            />

            <span
              className='smallDot'
              id={group.fullyQualifiedIdentifier}
              style={{ backgroundColor: groupAssessmentColor }}
              onClick={() => onGroupAssessmentClick(group.fullyQualifiedIdentifier)}
            />
            <StatusChip name={groupAssessmentField} status={groupAssessmentColor} />
          </Card>
        );
      }

      return null;
    });

    return (
      <Card className={classes.groupCard} variant={'outlined'}>
        <Typography variant={'h6'}>Group Status</Typography>
        <Grid container spacing={0}>
          {elements}
        </Grid>
      </Card>
    );
  };

  if (!landscape) return null;
  return (
    <Grid container spacing={3}>
      <Grid item xs={12} style={{ padding: 0 }}>
        {getGroupsCard(landscape.groups)}
      </Grid>
      <Grid item xs={12} style={{ padding: 5 }}>
        <Typography variant={'h6'}>Item Status</Typography>
      </Grid>
      {landscape?.groups.map((group, i) => getItems(group))}
    </Grid>
  );
};

export default DashboardLayout;
