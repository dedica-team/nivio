import React, { useState, ReactElement, useEffect } from 'react';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Card,
  CardActions,
  CardHeader,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Theme,
  Typography,
} from '@material-ui/core';
import { get } from '../../../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import { IAssessmentProps, IItem } from '../../../../interfaces';
import { getItemIcon, getLabels, getLinks } from '../../Utils/utils';
import StatusChip from '../../../StatusChip/StatusChip';
import IconButton from '@material-ui/core/IconButton';
import { ExpandMore, FilterCenterFocus, MoreVertSharp } from '@material-ui/icons';
import componentStyles from '../../../../Ressources/styling/ComponentStyles';
import Chip from '@material-ui/core/Chip';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import Avatar from "@material-ui/core/Avatar";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    labels: {
      backgroundColor: theme.palette.primary.main,
    },
  })
);

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
  const extraClasses = useStyles();
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
      const primary = `${relation.name}`;
      let secondary = `${relation.description || ''} (${relation.type} ${relation.direction})`;
      if (relation.format) secondary += ', format: ' + relation.format;
      relations.push(
        <ListItem key={relation.name}>
          <ListItemIcon>
            <IconButton
              onClick={() => {
                if (locateItem) {
                  locateItem(isInbound ? relation.source : relation.target);
                }
              }}
            >
              <FilterCenterFocus />
            </IconButton>
          </ListItemIcon>
          <ListItemText primary={primary} secondary={secondary} />
        </ListItem>
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
      >
        <FilterCenterFocus />
      </IconButton>
    ) : null;

  const labels = item ? getLabels(item) : null;
  const extend = small ? (
    <IconButton onClick={() => setCompact(!compact)}>
      <MoreVertSharp />
    </IconButton>
  ) : null;
  return (
    <Card className={classes.card}>
      <CardHeader
        title={item ? item.name || item.identifier : null}
        avatar={
          item ? <Avatar
              imgProps={{ style: { objectFit: 'contain' } }}
              src={getItemIcon(item)}
              style={{ backgroundColor: 'rgba(255, 255, 255, 0.5)', border: '2px solid #' + item.color }}
          />  : ''
        }
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
            <div className='tags'>
              {item
                ? item.tags.map((value) => (
                    <Chip size='small' variant='outlined' label={value} key={value} />
                  ))
                : null}
            </div>

            <List dense={true}>
              {item?.contact?.length ? (
                <ListItem>
                  <ListItemText
                    primary={'Contact'}
                    secondary={item?.contact || 'No contact provided'}
                  />
                </ListItem>
              ) : null}

              {item?.owner?.length ? (
                <ListItem>
                  <ListItemText primary={'Owner'} secondary={item?.owner || 'No owner provided'} />
                </ListItem>
              ) : null}
            </List>
          </div>

          {labels ? (
            <Accordion className={extraClasses.labels}>
              <AccordionSummary
                expandIcon={<ExpandMore />}
                aria-controls='panel1a-content'
                id='panel1a-header'
              >
                more
              </AccordionSummary>
              <AccordionDetails>{labels}</AccordionDetails>
            </Accordion>
          ) : null}

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
            <Box m={1}>
              <Typography variant={'h6'}>Relations</Typography>
              <List dense={true}>{relations}</List>
            </Box>
          ) : (
            ''
          )}
        </CardActions>
      ) : null}
    </Card>
  );
};
export default Item;
