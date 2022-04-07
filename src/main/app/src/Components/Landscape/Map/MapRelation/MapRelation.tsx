import React, { ReactElement, useContext, useState } from 'react';
import {
  Button,
  Card,
  CardHeader,
  Table,
  TableBody,
  TableCell,
  TableRow,
  useTheme,
} from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import { IItem, IRelation } from '../../../../interfaces';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import ItemAvatar from '../../Modals/Item/ItemAvatar';
import { Close, InfoOutlined } from '@material-ui/icons';
import { LandscapeContext } from '../../../../Context/LandscapeContext';
import { getLabels } from '../../Utils/utils';
import MappedString from '../../Utils/MappedString';

interface Props {
  source: IItem;
  target: IItem;
  relation: IRelation;
}

/**
 /**
 * Returns a chosen Map Relation
 *
 */
const MapRelation: React.FC<Props> = ({ source, target, relation }) => {
  const classes = componentStyles();
  const theme = useTheme();

  const [visible, setVisible] = useState<boolean>(true);
  const locateFunctionContext = useContext(LocateFunctionContext);
  const landscapeContext = useContext(LandscapeContext);

  if (!visible) return null;

  const sourceTitle = source.name || source.identifier;
  const targetTitle = target.name || target.identifier;
  const title = sourceTitle + ' to ' + targetTitle;
  const labels = relation ? getLabels(relation) : null;
  const processes: ReactElement[] = [];
  Object.keys(relation.processes).forEach((key) => {
    processes.push(
      <Button onClick={() => locateFunctionContext.locateFunction(relation.processes[key])} key={key}>
        {key}
      </Button>
    );
  });

  const sourceStatus = landscapeContext.getAssessmentSummary(source.fullyQualifiedIdentifier);
  const targetStatus = landscapeContext.getAssessmentSummary(target.fullyQualifiedIdentifier);

  return (
    <Card className={classes.card}>
      <CardHeader
        title={title}
        className={classes.cardHeader}
        action={
          <IconButton
            size={'small'}
            onClick={() => {
              setVisible(false);
            }}
          >
            <Close />
          </IconButton>
        }
      />
      <CardContent>
        <Typography variant={'h6'}><MappedString mapKey={'Relation'} /></Typography>
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
                  <InfoOutlined
                    style={{ color: theme.palette.info.main }}
                    fontSize='small'
                    data-testid={'InfoIconRelation'}
                  />
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

        {relation.processes && processes.length > 0 ? (
          <>
            <Typography variant={'h6'}>Processes</Typography>
            {processes}
          </>
        ) : null}

        {labels ? (
          <>
            <Typography variant={'h6'}>Labels</Typography>
            {labels}
          </>
        ) : null}
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
            <ItemAvatar item={source} statusColor={sourceStatus?.status || ''} />
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
            <ItemAvatar item={target} statusColor={targetStatus?.status || ''} />
          </IconButton>

          {targetTitle}
        </div>
      </CardContent>
    </Card>
  );
};

export default MapRelation;
