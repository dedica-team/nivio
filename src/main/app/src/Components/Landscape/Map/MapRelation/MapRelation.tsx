import React, { useContext } from 'react';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import { IItem, IRelation } from '../../../../interfaces';
import Typography from '@material-ui/core/Typography';
import { FilterCenterFocus } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import Avatar from "@material-ui/core/Avatar";
import { getItemIcon } from "../../Utils/utils";

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
          <IconButton
            onClick={() => {
              locateFunctionContext.locateFunction(source.fullyQualifiedIdentifier);
            }}
            size={'small'}
            title={'Click to locate'}
          >
            <Avatar
              imgProps={{ style: { objectFit: 'contain' } }}
              src={getItemIcon(source)}
              style={{
                backgroundColor: 'rgba(255, 255, 255, 0.75)',
                border: '2px solid #' + source.color,
              }}
            />
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
            <Avatar
              imgProps={{ style: { objectFit: 'contain' } }}
              src={getItemIcon(target)}
              style={{
                backgroundColor: 'rgba(255, 255, 255, 0.75)',
                border: '2px solid #' + target.color,
              }}
            />
          </IconButton>

          {targetTitle}
        </div>
      </CardContent>
    </Card>
  );
};

export default MapRelation;
