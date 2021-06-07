import React, { useContext, useState } from 'react';
import { IGroup } from '../../../interfaces';
import StatusChip from '../../StatusChip/StatusChip';
import Button from '@material-ui/core/Button';
import { Card, CardHeader } from '@material-ui/core';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import componentStyles from '../../../Resources/styling/ComponentStyles';
import IconButton from '@material-ui/core/IconButton';
import { Close } from '@material-ui/icons';

interface Props {
  onItemClick: Function;
  onGroupClick: Function;
}

/**
 * Displays all groups of given landscape and provides all needed navigation
 */
const StatusBarLayout: React.FC<Props> = ({ onItemClick, onGroupClick }) => {
  const context = useContext(LandscapeContext);
  const componentClasses = componentStyles();
  const [visible, setVisible] = useState<boolean>(true);

  const close = (
    <IconButton
      onClick={() => {
        setVisible(false);
      }}
    >
      <Close />
    </IconButton>
  );

  const getItems = (group: IGroup) => {
    return group.items.map((item) => {
      const assessmentSummary = context.getAssessmentSummary(item.fullyQualifiedIdentifier);

      if (assessmentSummary?.field === '') return null;
      if (
        !assessmentSummary?.status ||
        assessmentSummary?.status === 'GREEN' ||
        assessmentSummary.status === 'UNDEFINED'
      )
        return null;
      if (!assessmentSummary.maxField) return null;

      return (
        <Button key={item.fullyQualifiedIdentifier} onClick={() => onItemClick(item)}>
          <StatusChip
            name={(item.name || item.identifier) + ' ' + assessmentSummary?.maxField}
            status={assessmentSummary?.status}
          />
        </Button>
      );
    });
  };

  const getGroups = (groups: IGroup[]) => {
    if (!groups) return;

    return groups.map((group) => {
      if (group.items.length === 0) {
        console.log('Skipping group without items');
        return null;
      }

      const groupColor = `#${group.color}` || 'grey';
      const groupAssessment = context.getAssessmentSummary(group.fullyQualifiedIdentifier);
      if (!groupAssessment) return null;

      if (groupAssessment.field === '') {
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
            status={groupAssessment.status}
            style={{
              backgroundColor: groupColor,
            }}
          />
        </Button>
      );
    });
  };

  if (!visible) return null;
  return (
    <Card className={componentClasses.card}>
      <CardHeader title={'Warnings'} action={<React.Fragment>{close}</React.Fragment>} />
      {context.landscape ? getGroups(context.landscape.groups) : null}
      {context.landscape?.groups.map((group, i) => getItems(group))}
    </Card>
  );
};

export default StatusBarLayout;
