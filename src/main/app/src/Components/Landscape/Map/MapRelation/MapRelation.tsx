import React from 'react';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../../../Ressources/styling/ComponentStyles';
import { IItem, IRelation } from '../../../../interfaces';
import Typography from '@material-ui/core/Typography';
import { FilterCenterFocus } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';

interface Props {
  source: IItem;
  target: IItem;
  relation: IRelation;
  locateItem: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a chosen Map Relation
 *
 */
const MapRelation: React.FC<Props> = ({ source, target, relation, locateItem }) => {
  const classes = componentStyles();
  const sourceTitle = source.name || source.identifier;
  const targetTitle = target.name || target.identifier;
  const title = sourceTitle + ' -> ' + targetTitle;

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
          <IconButton
            onClick={() => {
              locateItem(source.fullyQualifiedIdentifier);
            }}
          >
            <FilterCenterFocus />
          </IconButton>
          {sourceTitle}
        </div>

        <Typography variant={'h6'}>Target</Typography>
        <div>
          <IconButton
            onClick={() => {
              locateItem(target.fullyQualifiedIdentifier);
            }}
          >
            <FilterCenterFocus />
          </IconButton>

          {targetTitle}
        </div>
      </CardContent>
    </Card>
  );
};

export default MapRelation;
