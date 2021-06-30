import React, { ReactElement, useContext, useEffect, useState } from 'react';
import { IChange, INotificationMessage } from '../../interfaces';
import { Card, CardHeader, Table, TableBody, TableCell, TableRow } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import { Alert } from '@material-ui/lab';
import { get } from '../../utils/API/APIClient';
import IconButton from '@material-ui/core/IconButton';
import ItemAvatar from '../Landscape/Modals/Item/ItemAvatar';
import componentStyles from '../../Resources/styling/ComponentStyles';
import { LocateFunctionContext } from '../../Context/LocateFunctionContext';
import GroupAvatar from '../Landscape/Modals/Group/GroupAvatar';
import { Close, LinkOutlined } from '@material-ui/icons';
import Button from '@material-ui/core/Button';

interface Props {
  notification: INotificationMessage;
}

/**
 * Displays the changes of an ProcessingFinishedEvent
 *
 * @param notification
 * @constructor
 */
const Changes: React.FC<Props> = ({ notification }) => {
  const componentClasses = componentStyles();
  const [changes, setChanges] = useState<ReactElement[]>([]);
  const locateFunctionContext = useContext(LocateFunctionContext);
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

  /**
   * render changes, calling api for component info
   */
  useEffect(() => {
    if (notification == null) return;

    const getItemChange = (key: string, change: IChange): Promise<any> => {
      if (change.changeType === 'DELETED') {
        return new Promise((resolve) =>
          resolve(
            <TableRow key={key}>
              <TableCell style={{ width: '25%' }}>Item {key}</TableCell>
              <TableCell>{change.changeType}</TableCell>
            </TableRow>
          )
        );
      }
      return get(`/api/${key}`).then((item) => (
        <TableRow key={key}>
          <TableCell style={{ width: '25%' }}>
            <IconButton title={key} onClick={() => locateFunctionContext.locateFunction(key)}>
              <ItemAvatar item={item} statusColor={''} />
            </IconButton>
          </TableCell>
          <TableCell>{change.message}</TableCell>
        </TableRow>
      ));
    };

    const getGroupChange = (key: string, change: IChange): Promise<any> => {
      if (change.changeType === 'DELETED') {
        return new Promise((resolve) =>
          resolve(
            <TableRow key={key}>
              <TableCell style={{ width: '25%' }}>Group {key}</TableCell>
              <TableCell>{change.changeType}</TableCell>
            </TableRow>
          )
        );
      }
      return get(`/api/${key}`).then((group) => (
        <TableRow key={key}>
          <TableCell style={{ width: '25%' }}>
            <IconButton
              onClick={() => locateFunctionContext.locateFunction(key)}
              title={`Click to locate group ${group.identifier}`}
            >
              <GroupAvatar group={group} statusColor={''} />
            </IconButton>
          </TableCell>
          <TableCell>{change.message}</TableCell>
        </TableRow>
      ));
    };

    const getRelationChange = (key: string, change: IChange): Promise<any> => {
      const parts = key.split(';');
      const buttonText = (fqi: string): string => {
        const parts = fqi.trim().split('/');
        return `${parts[1]}/${parts[2]}`;
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
              {change.changeType} Relation from{' '}
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

    if (notification.changelog != null) {
      let promises: Promise<any>[] = [];
      for (let key of Object.keys(notification.changelog.changes)) {
        let change = notification.changelog.changes[key];

        switch (change.componentType) {
          case 'Item':
            promises.push(getItemChange(key, change));
            break;
          case 'Group':
            promises.push(getGroupChange(key, change));
            break;
          case 'Relation':
            promises.push(getRelationChange(key, change));
            break;
        }
      }
      Promise.all<ReactElement>(promises).then((rows) => {
        setChanges(rows);
      });
    }
  }, [notification, componentClasses.card, locateFunctionContext]);

  if (!visible) return null;

  return (
    <Card className={componentClasses.card}>
      <CardHeader title={'Last change in ' + notification.landscape} action={close} />
      <CardContent>
        <Alert severity={notification.level}>
          {notification.date} {notification.landscape}
          <br />
          {notification.message}
        </Alert>
      </CardContent>
      <Table aria-label={'changes'} style={{ tableLayout: 'fixed' }}>
        <TableBody>{notification.changelog != null ? changes : null}</TableBody>
      </Table>
    </Card>
  );
};

export default Changes;
