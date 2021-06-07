import React, { useContext, useState } from 'react';
import { Card, CardHeader, Table, TableBody, TableCell, TableRow } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import { IItem, IRelation } from '../../../../interfaces';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import ItemAvatar from '../../Modals/Item/ItemAvatar';
import { Close, HelpRounded } from '@material-ui/icons';

interface Props {
  source: IItem;
  target: IItem;
  relation: IRelation;
}

/**
 * Returns a chosen Map Relation
 *
 */
const MapRelation: React.FC<Props> = ({ source, target, relation }) => {
  const classes = componentStyles();
  const sourceTitle = source.name || source.identifier;
  const targetTitle = target.name || target.identifier;
  const title = sourceTitle + ' to ' + targetTitle;
  const [visible, setVisible] = useState<boolean>(true);
  const locateFunctionContext = useContext(LocateFunctionContext);

  const close = (
    <IconButton
      onClick={() => {
        setVisible(false);
      }}
    >
      <Close />
    </IconButton>
  );

  if (!visible) return null;

  return (
    <Card className={classes.card}>
      <CardHeader
        title={title}
        className={classes.cardHeader}
        subheader={'Relation'}
        action={close}
      />
      <CardContent>
        <Table aria-label={'info table'} style={{ tableLayout: 'fixed' }}>
          <TableBody>
            <TableRow key={'Type'}>
              <TableCell style={{ width: '33%' }}>Type</TableCell>
              <TableCell>
                {relation.type}
                <span
                  title={
                    'A PROVIDER relation is a hard dependency that is required. A DATAFLOW relation is a soft dependency.'
                  }
                >
                  <HelpRounded />
                </span>
              </TableCell>
            </TableRow>

            <TableRow key={'format'}>
              <TableCell style={{ width: '33%' }}>Format</TableCell>
              <TableCell>{relation.format || '-'}</TableCell>
            </TableRow>

            <TableRow key={'desc'}>
              <TableCell style={{ width: '33%' }}>Description</TableCell>
              <TableCell>{relation.description || '-'}</TableCell>
            </TableRow>
          </TableBody>
        </Table>
        <br />
        <Typography variant={'h6'}>Source</Typography>
        <div>
          <IconButton
            onClick={() => {
              locateFunctionContext.locateFunction(source.fullyQualifiedIdentifier);
            }}
            size={'small'}
            title={'Click to locate'}
          >
            <ItemAvatar item={source} statusColor={''} />
          </IconButton>

          {sourceTitle}
        </div>

        <Typography variant={'h6'}>Target</Typography>
        <div>
          <IconButton
            onClick={() => {
              locateFunctionContext.locateFunction(target.fullyQualifiedIdentifier);
            }}
            size={'small'}
            title={'Click to locate'}
          >
            <ItemAvatar item={target} statusColor={''} />
          </IconButton>

          {targetTitle}
        </div>
      </CardContent>
    </Card>
  );
};

export default MapRelation;
