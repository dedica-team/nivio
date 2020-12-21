import React, { useState, ReactElement, useEffect } from 'react';
import { Card, CardActions, CardHeader, Theme, Typography } from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import { IAssessmentProps, IItem } from '../../../interfaces';
import { getItemIcon, getLabels, getLinks } from '../Utils/utils';
import Button from '@material-ui/core/Button';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import StatusChip from '../../StatusChip/StatusChip';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      margin: 5,
      padding: 5,
      backgroundColor: theme.palette.secondary.main,
      overflow: 'show',
    },
    icon: {
      height: '2em',
    },
  })
);

interface Props {
  small?: boolean;
  useItem?: IItem;
  findItem?: Function;
  fullyQualifiedItemIdentifier?: string;
  onAssessmentClick?: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 */
const SearchResult: React.FC<Props> = ({
  useItem,
  findItem,
  fullyQualifiedItemIdentifier,
  small,
}) => {
  const [assessment, setAssessment] = useState<IAssessmentProps[] | undefined>(undefined);
  const [item, setItem] = useState<IItem | undefined>(undefined);
  const [compact, setCompact] = useState<boolean>(false);
  const classes = useStyles();
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

  if (item && item?.relations && item.relations.length) {
    relations = item.relations.map((relation) => {
      let relationName: string;
      let groupNameStart: number;
      if (relation.target.endsWith(item.identifier)) {
        groupNameStart = relation.source.indexOf('/') + 1;
        relationName = relation.source.substr(groupNameStart);
        return (
          <Button
            size={'small'}
            key={relation.source}
            onClick={() => {
              if (findItem) {
                findItem(relation.source);
              }
            }}
          >
            {relationName}
          </Button>
        );
      }
      groupNameStart = relation.target.indexOf('/') + 1;
      relationName = relation.target.substr(groupNameStart);
      return (
        <Button
          size={'small'}
          key={relation.target}
          onClick={() => {
            if (findItem) {
              findItem(relation.target);
            }
          }}
        >
          {relationName}
        </Button>
      );
    });
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

  const assessmentStatuses = assessment ? getItemAssessments(assessment) : [];
  const links: ReactElement[] = item ? getLinks(item) : [];

  return (
    <Card className={classes.card}>
      <CardHeader
        title={item ? item.name || item.identifier : null}
        avatar={item ? <img src={getItemIcon(item)} alt='Icon' className={classes.icon} /> : ''}
        onClick={() => {
          //can only be toggled if once small
          if (small) {
            setCompact(!compact);
          }
          if (findItem && item) {
            findItem(item.fullyQualifiedIdentifier);
          }
        }}
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

          <div className={'statuses'}>
            <Typography variant={'h6'}>Statuses</Typography>
            {assessmentStatuses ? <div>{assessmentStatuses}</div> : '-'}
          </div>

          <div className='links'>
            <Typography variant={'h6'}>Links</Typography>
            <br />
            {links}
          </div>
        </CardContent>
      ) : null}

      {!compact ? (
        <CardActions>
          <div className='relations'>
            <Typography variant={'h6'}>Relations</Typography>
            <br />
            {relations ?? { relations }}
          </div>
        </CardActions>
      ) : null}
    </Card>
  );
};
export default SearchResult;
