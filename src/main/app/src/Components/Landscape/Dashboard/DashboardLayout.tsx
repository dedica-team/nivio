import React from 'react';

import Grid from '@material-ui/core/Grid';
import { IAssessment, IGroup, ILandscape } from '../../../interfaces';
import { getAssessmentSummary } from '../Utils/utils';
import StatusChip from '../../StatusChip/StatusChip';
import Button from '@material-ui/core/Button';

interface Props {
  landscape: ILandscape;
  assessments: IAssessment;
  onItemClick: Function;
  onGroupClick: Function;
}

/**
 * Displays all groups of given landscape and provides all needed navigation
 */
const DashboardLayout: React.FC<Props> = ({
  landscape,
  assessments,
  onItemClick,
  onGroupClick,
}) => {

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
      if (group.items.length > 0) {
        const groupColor = `#${group.color}` || 'grey';
        const [groupAssessmentColor, , groupAssessmentField] = getAssessmentSummary(
          assessments?.results[group.fullyQualifiedIdentifier]
        );

        if (groupAssessmentField === '') return null;

        const title = 'Group ' + group.name;
        return (
          <Button
            id={group.fullyQualifiedIdentifier}
            onClick={() => onGroupClick(group.fullyQualifiedIdentifier)}
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
      }

      return null;
    });
  };

  return (
    <Grid container spacing={3}>
      {getGroups(landscape.groups)}
      {landscape?.groups.map((group, i) => getItems(group))}
    </Grid>
  );
};

export default DashboardLayout;
