import React, { ReactElement, useContext, useEffect, useState } from 'react';
import { IChange } from '../../interfaces';
import { Box, Table, TableBody, TableCell, TableRow, Typography } from '@material-ui/core';
import { get } from '../../utils/API/APIClient';
import IconButton from '@material-ui/core/IconButton';
import ItemAvatar from '../Landscape/Modals/Item/ItemAvatar';
import componentStyles from '../../Resources/styling/ComponentStyles';
import { LocateFunctionContext } from '../../Context/LocateFunctionContext';
import GroupAvatar from '../Landscape/Modals/Group/GroupAvatar';
import { LinkOutlined } from '@material-ui/icons';
import Button from '@material-ui/core/Button';
import { LandscapeContext } from '../../Context/LandscapeContext';

/**
 * Displays the changes of an ProcessingFinishedEvent
 *
 * @constructor
 */
const Changes: React.FC = () => {
  const componentClasses = componentStyles();
  const [renderedChanges, setRenderedChanges] = useState<ReactElement[]>([]);
  const locateFunctionContext = useContext(LocateFunctionContext);
  const landscapeContext = useContext(LandscapeContext);

  /**
   * render changes, calling api for component info
   */
  useEffect(() => {
    if (
      landscapeContext.landscapeChanges == null ||
      landscapeContext.landscapeChanges.landscape !== landscapeContext.identifier
    ) {
      setRenderedChanges([]);
      return;
    }

    /**
     * Generates a merged ul-li change list
     * @param componentChange structural changes
     * @param assessmentChange status changes
     */
    const changeList = (
      componentChange: IChange | null,
      assessmentChange: IChange | null
    ): JSX.Element | null => {
      const items: JSX.Element[] = [];
      if (componentChange)
        componentChange.messages.forEach((value, index) =>
          items.push(<li key={'c_' + index}>{value}</li>)
        );
      if (assessmentChange)
        assessmentChange.messages.forEach((value, index) =>
          items.push(<li key={'a_' + index}>{value}</li>)
        );
      if (items.length === 0) return null;
      return <ul>{items}</ul>;
    };

    const getItemChange = (
      key: string,
      componentChange: IChange | null,
      assessmentChange: IChange | null
    ): Promise<any> => {
      if (componentChange && componentChange.changeType === 'DELETED') {
        return new Promise((resolve) =>
          resolve(
            <TableRow key={key}>
              <TableCell style={{ width: '25%' }}>Item {key}</TableCell>
              <TableCell>{componentChange.changeType}</TableCell>
            </TableRow>
          )
        );
      }

      return get(`/api/${key}`).then((item) => {
        const assessment = landscapeContext.getAssessmentSummary(item.fullyQualifiedIdentifier);
        const changeList1 = changeList(componentChange, assessmentChange);
        if (!changeList1) return null;
        return (
          <TableRow key={key}>
            <TableCell style={{ width: '25%' }}>
              <IconButton title={key} onClick={() => locateFunctionContext.locateFunction(key)}>
                <ItemAvatar item={item} statusColor={assessment ? assessment.status : ''} />
              </IconButton>
            </TableCell>
            <TableCell>{changeList1}</TableCell>
          </TableRow>
        );
      });
    };

    const getGroupChange = (
      key: string,
      componentChange: IChange | null,
      assessmentChange: IChange | null
    ): Promise<any> => {
      if (componentChange && componentChange.changeType === 'DELETED') {
        return new Promise((resolve) =>
          resolve(
            <TableRow key={key}>
              <TableCell style={{ width: '25%' }}>Group {key}</TableCell>
              <TableCell>{componentChange.changeType}</TableCell>
            </TableRow>
          )
        );
      }

      return get(`/api/${key}`).then((group) => {
        const assessment = landscapeContext.getAssessmentSummary(group.fullyQualifiedIdentifier);
        const changeList1 = changeList(componentChange, assessmentChange);
        if (!changeList1) return null;
        return (
          <TableRow key={key}>
            <TableCell style={{ width: '25%' }}>
              <IconButton
                onClick={() => locateFunctionContext.locateFunction(key)}
                title={`Click to locate group ${group.identifier}`}
              >
                <GroupAvatar group={group} statusColor={assessment ? assessment.status : ''} />
              </IconButton>
            </TableCell>
            <TableCell>{changeList1}</TableCell>
          </TableRow>
        );
      });
    };

    const getRelationChange = (
      key: string,
      componentChange: IChange | null,
      assessmentChange: IChange | null
    ): Promise<any> => {
      const parts = key.split(';');
      const buttonText = (fqi: string): string => {
        const strings = fqi.trim().split('/');
        return `${strings[1]}/${strings[2]}`;
      };

      if (!componentChange)
        componentChange = {
          changeType: 'UPDATE',
          componentType: 'RELATION',
          messages: [],
        };
      return new Promise((resolve) =>
        resolve(
          <TableRow key={key}>
            <TableCell style={{ width: '25%' }}>
              <IconButton title={key} onClick={() => locateFunctionContext.locateFunction(key)}>
                <LinkOutlined />
              </IconButton>
            </TableCell>
            <TableCell>
              {componentChange?.changeType} Relation from{' '}
              <Button
                onClick={() => locateFunctionContext.locateFunction(parts[0])}
                size='small'
                variant={'outlined'}
              >
                {buttonText(parts[0])}
              </Button>{' '}
              to{' '}
              <Button
                onClick={() => locateFunctionContext.locateFunction(parts[1])}
                size='small'
                variant={'outlined'}
              >
                {buttonText(parts[1])}
              </Button>
            </TableCell>
          </TableRow>
        )
      );
    };

    class ChangeSet {
      componentChange: IChange | null = null;
      assessmentChange: IChange | null = null;
    }

    const changes: Map<string, ChangeSet> = new Map();

    if (landscapeContext.landscapeChanges.changelog) {
      for (let key of Object.keys(landscapeContext.landscapeChanges.changelog.changes)) {
        if (!changes.has(key)) changes.set(key, new ChangeSet());
        // @ts-ignore
        changes.get(key).componentChange = landscapeContext.landscapeChanges.changelog.changes[key];
      }
    }

    if (landscapeContext.assessmentChanges?.changelog) {
      for (let key of Object.keys(landscapeContext.assessmentChanges.changelog.changes)) {
        if (!changes.has(key)) changes.set(key, new ChangeSet());
        // @ts-ignore
        changes.get(key).assessmentChange =
          landscapeContext.assessmentChanges.changelog.changes[key];
      }
    }

    let promises: Promise<any>[] = [];

    changes.forEach((value, key) => {
      let change = value.componentChange || value.assessmentChange;
      if (!change) return;
      switch (change.componentType) {
        case 'Item':
          promises.push(getItemChange(key, value.componentChange, value.assessmentChange));
          break;
        case 'Group':
          promises.push(getGroupChange(key, value.componentChange, value.assessmentChange));
          break;
        case 'Relation':
          promises.push(getRelationChange(key, value.componentChange, value.assessmentChange));
          break;
      }
    });

    Promise.all<ReactElement>(promises).then((rows) => {
      setRenderedChanges(rows);
    });
  }, [componentClasses.card, locateFunctionContext, landscapeContext]);

  return (
    <Box>
      <div>
        <Typography variant={'h5'}>Latest changes</Typography>
        <Typography variant={'h6'}>{landscapeContext.landscape?.name}</Typography>
      </div>
      {landscapeContext.landscapeChanges != null ? (
        <Table aria-label={'changes'} style={{ tableLayout: 'fixed' }}>
          <TableBody>{renderedChanges}</TableBody>
        </Table>
      ) : (
        <Typography>No changes recorded yet.</Typography>
      )}
    </Box>
  );
};

export default Changes;
