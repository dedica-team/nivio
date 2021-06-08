import React, { useContext, useState } from 'react';
import { IGroup } from '../../../interfaces';
import StatusChip from '../../StatusChip/StatusChip';
import Button from '@material-ui/core/Button';
import { Card, CardHeader, Table, TableBody, TableCell, TableRow } from '@material-ui/core';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import componentStyles from '../../../Resources/styling/ComponentStyles';
import IconButton from '@material-ui/core/IconButton';
import { Close } from '@material-ui/icons';
import ItemAvatar from '../Modals/Item/ItemAvatar';
import GroupAvatar from '../Modals/Group/GroupAvatar';

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
        <TableRow>
          <TableCell style={{ textAlign: 'center' }}>
            <Button
              key={item.fullyQualifiedIdentifier}
              title={item.name || item.identifier}
              onClick={() => onItemClick(item)}
            >
              <ItemAvatar item={item} statusColor={assessmentSummary?.status} />
            </Button>
            <br />
            {item.name || item.identifier}
          </TableCell>
          <TableCell>
            <StatusChip name={assessmentSummary.maxField} status={assessmentSummary.status} />
            {assessmentSummary.message}
          </TableCell>
        </TableRow>
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

      const groupAssessment = context.getAssessmentSummary(group.fullyQualifiedIdentifier);
      if (
        !groupAssessment ||
        !groupAssessment?.status ||
        groupAssessment?.status === 'GREEN' ||
        groupAssessment.status === 'UNDEFINED'
      )
        return null;

      if (groupAssessment.field === '') {
        console.debug('Group ' + group.fullyQualifiedIdentifier + ' has no summary assessment');
        return null;
      }

      return (
        <TableRow>
          <TableCell style={{ textAlign: 'center' }}>
            <Button
              id={group.fullyQualifiedIdentifier}
              onClick={() => onGroupClick(group)}
              key={group.name}
            >
              <GroupAvatar group={group} statusColor={groupAssessment.status} />
            </Button>
            <br />
            {group.name}
          </TableCell>
          <TableCell>
            <StatusChip name={groupAssessment.maxField} status={groupAssessment.status} />
            {groupAssessment.message}
          </TableCell>
        </TableRow>
      );
    });
  };

  if (!visible) return null;
  return (
    <Card className={componentClasses.card}>
      <CardHeader
        title={'Warnings'}
        action={
          <IconButton
            onClick={() => {
              setVisible(false);
            }}
          >
            <Close />
          </IconButton>
        }
      />
      <Table>
        <TableBody>{context.landscape ? getGroups(context.landscape.groups) : null}</TableBody>
      </Table>
      {context.landscape?.groups.map((group) => getItems(group))}
    </Card>
  );
};

export default StatusBarLayout;
