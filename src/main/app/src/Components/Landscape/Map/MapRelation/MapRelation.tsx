import React, { useContext } from 'react';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import Button from '@material-ui/core/Button';
import { IItem, IRelation } from '../../../../interfaces';
import Typography from '@material-ui/core/Typography';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';

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
  const title = sourceTitle + ' -> ' + targetTitle;

  const locateFunctionContext = useContext(LocateFunctionContext);

  return (
    <Card className={classes.card}>
      <CardHeader title={title} className={classes.cardHeader} subheader={'Relation'} />
      <CardContent>
        <span>Type: {relation.type || '-'}</span>
        <br />
        <span>Format: {relation.format || '-'}</span>
        <br />
        <span>Description: {relation.description || '-'}</span>
        <br />
        <br />
        <Typography variant={'h6'}>Source</Typography>
        <div>
          <Button
            fullWidth={true}
            key={source.fullyQualifiedIdentifier}
            onClick={() => {
              locateFunctionContext.locateFunction(source.fullyQualifiedIdentifier);
            }}
          >
            {sourceTitle}
          </Button>
        </div>

        <Typography variant={'h6'}>Target</Typography>
        <div>
          <Button
            fullWidth={true}
            key={target.fullyQualifiedIdentifier}
            onClick={() => {
              locateFunctionContext.locateFunction(target.fullyQualifiedIdentifier);
            }}
          >
            {targetTitle}
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};

export default MapRelation;
