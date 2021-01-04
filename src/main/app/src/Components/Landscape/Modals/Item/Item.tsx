import React, { useState, ReactElement, useEffect } from 'react';
import { Card, CardActions, CardHeader, Paper, Typography } from '@material-ui/core';
import { get } from '../../../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import { IAssessmentProps, IItem } from '../../../../interfaces';
import { getItemIcon, getLabels, getLinks } from '../../Utils/utils';
import Button from '@material-ui/core/Button';
import StatusChip from '../../../StatusChip/StatusChip';
import IconButton from '@material-ui/core/IconButton';
import { ArrowDownward, ArrowUpward, FilterCenterFocus, MoreVertSharp } from '@material-ui/icons';
import componentStyles from '../../../../Ressources/styling/ComponentStyles';

interface Props {
  small?: boolean;
  useItem?: IItem;
  locateItem?: Function;
  fullyQualifiedItemIdentifier?: string;
}

/**
 * Returns a chosen Landscape item if information is available
 *
 *
 */
const Item: React.FC<Props> = ({ useItem, locateItem, fullyQualifiedItemIdentifier, small }) => {
  const [assessment, setAssessment] = useState<IAssessmentProps[] | undefined>(undefined);
  const [item, setItem] = useState<IItem | undefined>(undefined);
  const [compact, setCompact] = useState<boolean>(false);
  const classes = componentStyles();
  let relations: ReactElement[] = [];

  useEffect(() => {
    const loadAssessment = (item: IItem) => {
      const landscapeIdentifier = item ? item.fullyQualifiedIdentifier.split('/') : [];

      if (landscapeIdentifier[0]) {
        //TODO load direct to with fqi in path
        get(`/assessment/${landscapeIdentifier[0]}`).then((response) => {
          if (response) {
            // @ts-ignore
            setAssessment(response.results[item?.fullyQualifiedIdentifier]);
          }
        });
      }
    };

    const reset = (item: IItem) => {
      setItem(item);
      setAssessment(undefined);
      loadAssessment(item);
    };

    if (useItem) {
      if (useItem && item !== useItem) {
        reset(useItem);
      }
    } else {
      if (!item && fullyQualifiedItemIdentifier) {
        get(`/api/${fullyQualifiedItemIdentifier}`).then((loaded) => {
          reset(loaded);
        });
      }
    }

    if (small) {
      setCompact(true);
    }
  }, [item, fullyQualifiedItemIdentifier, useItem, small, assessment]);

  if (item) {
    for (let key of Object.keys(item.relations)) {
      let relation = item.relations[key];
      const isInbound = relation.direction === 'inbound';
      relations.push(
        <Paper style={{ width: '100%', padding: 5, marginTop: 5 }} key={key}>
          <Button
            size={'small'}
            fullWidth={true}
            onClick={() => {
              if (locateItem) {
                locateItem(isInbound ? relation.source : relation.target);
              }
            }}
          >
            {isInbound ? <ArrowDownward /> : <ArrowUpward />}
            {relation.name}
          </Button>
          {relation.direction} {relation.description?.length ? ', ' + relation.description : null}
          {relation.format?.length ? ', format: ' + relation.format : null}
        </Paper>
      );
    }
  }

  const getItemAssessments = (assessmentItem: IAssessmentProps[]) => {
    if (item && assessmentItem) {
      return assessmentItem
        .filter((item) => !item.field.includes('summary.'))
        .map((item) => {
          return (
            <StatusChip
              name={item.field}
              value={item.message}
              status={item.status}
              key={item.field}
            />
          );
        });
    }
    return [];
  };

  const assessmentStatus = assessment ? getItemAssessments(assessment) : [];
  const links: ReactElement[] = item ? getLinks(item) : [];

  const findButton =
    locateItem && item ? (
      <IconButton
        onClick={() => {
          locateItem(item.fullyQualifiedIdentifier);
        }}
        className={classes.floatingButton}
      >
        <FilterCenterFocus />
      </IconButton>
    ) : null;

  const extend = small ? (
    <IconButton onClick={() => setCompact(!compact)} className={classes.floatingButton}>
      <MoreVertSharp />
    </IconButton>
  ) : null;
  return (
    <Card className={classes.card}>
      <CardHeader
        title={item ? item.name || item.identifier : null}
        avatar={item ? <img src={getItemIcon(item)} alt='Icon' className={classes.icon} /> : ''}
        className={classes.cardHeader}
        action={
          <React.Fragment>
            {extend}
            {findButton}
          </React.Fragment>
        }
      />

      {!compact ? (
        <CardContent>
          <div className='information'>
            <span className='description item'>
              {item?.description ? `${item?.description}` : ''}
              <br />
            </span>
            {item?.contact?.length ? (
              <span className='contact item'>
                <span className='label'>Contact: </span>
                {item?.contact || 'No Contact provided'}
                <br />
              </span>
            ) : null}
            {item?.owner ?? (
              <span className='owner item'>
                <span className='label'>Owner: </span>
                {item?.owner || 'No Contact provided'}
                <br />
              </span>
            )}
            <br />
            <div className='labels'>{item ? getLabels(item) : null}</div>
          </div>

          {assessmentStatus.length > 0 ? (
            <div className={'status'}>
              <Typography variant={'h6'}>Status</Typography>
              {assessmentStatus ? <div>{assessmentStatus}</div> : '-'}
            </div>
          ) : null}

          {links.length > 0 ? (
            <div className='links'>
              <Typography variant={'h6'}>Links</Typography>
              <br />
              {links}
            </div>
          ) : null}
        </CardContent>
      ) : null}

      {!compact ? (
        <CardActions>
          {relations && relations.length ? (
            <div style={{ width: '100%' }}>
              <Typography variant={'h6'}>Relations</Typography>
              {relations}
            </div>
          ) : (
            ''
          )}
        </CardActions>
      ) : null}
    </Card>
  );
};
export default Item;
