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
              <TableCell style={{ width: '30%' }}>Item {key}</TableCell>
              <TableCell>{change.changeType}</TableCell>
            </TableRow>
          )
        );
      }
      return get(`/api/${key}`).then((item) => (
        <TableRow key={key}>
          <TableCell style={{ width: '30%' }}>
            <IconButton title={key} onClick={() => locateFunctionContext.locateFunction(key)}>
              <ItemAvatar item={item} statusColor={''} />
            </IconButton>
          </TableCell>
          <TableCell>{change.message}</TableCell>
        </TableRow>
      ));
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
            promises.push(
              new Promise((resolve) => {
                resolve(
                  <TableRow key={key}>
                    <TableCell style={{ width: '30%' }}>
                      {change.changeType} {change.componentType}
                    </TableCell>
                    <TableCell>{key}<br />{change.message}</TableCell>
                  </TableRow>
                );
              })
            );
            break;
          case 'Relation':
            promises.push(
              new Promise((resolve) => {
                resolve(
                  <TableRow key={key}>
                    <TableCell style={{ width: '30%' }}>
                      {change.changeType} {change.componentType}
                    </TableCell>
                    <TableCell>
                      {key}
                      <br />
                      {change.message}
                    </TableCell>
                  </TableRow>
                );
              })
            );
            break;
        }
      }
      Promise.all<ReactElement>(promises).then((rows) => {
        setChanges(rows);
      });
    }
  }, [notification, componentClasses.card, locateFunctionContext]);

  return (
    <Card className={componentClasses.card}>
      <CardHeader title={notification.landscape} />
      <CardContent>
        <Alert severity={notification.level}>
          {notification.date} {notification.landscape}
          <br />
          {notification.message}
        </Alert>
        <br />
        <Table aria-label={'changes'} style={{ tableLayout: 'fixed' }}>
          <TableBody>{notification.changelog != null ? changes : null}</TableBody>
        </Table>
      </CardContent>
    </Card>
  );
};

export default Changes;
