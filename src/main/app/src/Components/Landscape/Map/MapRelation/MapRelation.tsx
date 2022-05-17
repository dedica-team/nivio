import React, { useContext, useEffect, useState } from 'react';
import {
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
import { getItem, getLabels } from '../../Utils/utils';
import MappedString from '../../Utils/MappedString';

interface Props {
  dataSource: string | null;
  defaultSource: IItem;
  target: IItem;
  relId: string;
}

/**
 /**
 * Returns a chosen Map Relation
 *
 */
const MapRelation: React.FC<Props> = ({ dataSource, defaultSource, target, relId }) => {
  const classes = componentStyles();
  const theme = useTheme();

  const [visible, setVisible] = useState<boolean>(true);
  const [relation, setRelation] = useState<IRelation>(defaultSource.relations[relId]);
  const [source, setSource] = useState<IItem>(defaultSource);
  const locateFunctionContext = useContext(LocateFunctionContext);
  const landscapeContext = useContext(LandscapeContext);

  useEffect(() => {
    if (!landscapeContext.landscape) {
      return;
    }
    if (!dataSource) {
      return;
    }
    setSource(getItem(landscapeContext.landscape, dataSource) || source);
    setRelation(source.relations[relId]);
  }, [landscapeContext.landscape, dataSource, relId, source]);

  if (!visible) return null;

  const sourceTitle = source.name || source.identifier;
  const targetTitle = target.name || target.identifier;
  const title = sourceTitle + ' to ' + targetTitle;
  const labels = relation ? getLabels(relation) : null;

  const sourceStatus = landscapeContext.getAssessmentSummary(source.fullyQualifiedIdentifier);
  const targetStatus = landscapeContext.getAssessmentSummary(target.fullyQualifiedIdentifier);

  return (
    <Card className={classes.card}>
      <CardHeader
        title={title}
        className={classes.cardHeader}
        subheader={<MappedString mapKey={'Relation'} />}
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
