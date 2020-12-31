import React from 'react';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../../../Ressources/styling/ComponentStyles';
import Button from '@material-ui/core/Button';

interface Props {
  sourceIdentifier: string;
  targetIdentifier: string;
  type: string | null;
  locateItem: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a chosen Map Relation
 *
 * TODO work with items and relation objects (loaded via API, get rid of string splitting)
 */
const MapRelation: React.FC<Props> = ({ sourceIdentifier, targetIdentifier, type, locateItem }) => {
  const sourceGroupNameStart = sourceIdentifier.indexOf('/') + 1;
  const sourceRelation = sourceIdentifier.substr(sourceGroupNameStart);
  const classes = componentStyles();
  const targetGroupNameStart = targetIdentifier.indexOf('/') + 1;
  const targetRelation = targetIdentifier.substr(targetGroupNameStart);

  const sourceTitle = sourceIdentifier.split('/').pop();
  const targetTitle = targetIdentifier.split('/').pop();

  const title = 'Relation ' + sourceTitle + ' ⇄ ' + targetTitle;
  return (
    <Card className={classes.card}>
      <CardHeader title={title} className={classes.cardHeader}/>
      <CardContent>
        <span>Type: {type}</span>
        <br />
        <br />
        Source:
        <br />
        <Button
          fullWidth={true}
          key={sourceIdentifier}
          onClick={() => {
            locateItem(sourceIdentifier);
          }}
        >
          {sourceRelation}
        </Button>
        <br />
        Target:
        <br />
        <Button
          fullWidth={true}
          key={targetIdentifier}
          onClick={() => {
            locateItem(targetIdentifier);
          }}
        >
          {targetRelation}
        </Button>
      </CardContent>
    </Card>
  );
};

export default MapRelation;
