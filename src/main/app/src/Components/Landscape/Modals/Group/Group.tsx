import React, {useContext} from 'react';

import { IAssessment, IGroup } from '../../../../interfaces';
import { getLabels, getLinks, getAssessmentSummary, getItemIcon } from '../../Utils/utils';
import { Badge, Card, CardHeader, Theme, withStyles } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import StatusChip from '../../../StatusChip/StatusChip';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import IconButton from "@material-ui/core/IconButton";
import {FilterCenterFocus} from "@material-ui/icons";
import {LocateFunctionContext} from '../../../../Context/LocateFunctionContext';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    groupAvatar: {
      width: 15,
      height: 15,
      display: 'inline-block',
    },
  })
);

const StyledBadge = withStyles((theme: Theme) =>
  createStyles({
    'badge': {
      'boxShadow': `0 0 0 2px ${theme.palette.background.paper}`,
      '&::after': {
        position: 'absolute',
        top: 0,
        left: 0,
        width: '100%',
        height: '100%',
        borderRadius: '50%',
        //animation: '$ripple 1.2s infinite ease-in-out',
        border: '1px solid currentColor',
        backgroundColor: 'currentColor',
        content: '""',
      },
    },
    '@keyframes ripple': {
      '0%': {
        transform: 'scale(.8)',
        opacity: 1,
      },
      '100%': {
        transform: 'scale(2.4)',
        opacity: 0,
      },
    },
  })
)(Badge);

interface Props {
  group: IGroup;
  assessments: IAssessment;
}

/**
 * Returns a chosen group if information is available
 */
const Group: React.FC<Props> = ({ group, assessments }) => {
  const componentClasses = componentStyles();
  const classes = useStyles();

  const locateFunctionContext = useContext(LocateFunctionContext);

  const getGroupItems = (
    group: IGroup,
    findItem?: (fullyQualifiedItemIdentifier: string) => void
  ) => {
    if (group?.items) {
      return group.items.map((item) => {
        const [status, ,] = getAssessmentSummary(
          assessments.results[item.fullyQualifiedIdentifier]
        );
        return (
          <Button
            style={{ textAlign: 'left' }}
            key={item.fullyQualifiedIdentifier}
            onClick={() => {
              if (findItem) {
                findItem(item.fullyQualifiedIdentifier);
              }
            }}
          >
            <StyledBadge
              overlap='circle'
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
              }}
              variant='dot'
              style={{ color: status }}
            >
              <Avatar
                imgProps={{ style: { objectFit: 'contain' } }}
                src={getItemIcon(item)}
                style={{ backgroundColor: 'white', border: '2px solid #' + group.color }}
              />
            </StyledBadge>
            &nbsp;
            {item.identifier}
          </Button>
        );
      });
    }
    return [];
  };
  const items = getGroupItems(group, locateFunctionContext.locateFunction);

  const [assessmentColor, message, field] = getAssessmentSummary(
    assessments.results[group.fullyQualifiedIdentifier]
  );
  const labels = getLabels(group);
  const links = getLinks(group);

  const action = locateFunctionContext.locateFunction ?
      <IconButton onClick={ () => locateFunctionContext.locateFunction(group.fullyQualifiedIdentifier)}><FilterCenterFocus /></IconButton>
      : null;
  return (
    <Card className={componentClasses.card}>
      <CardHeader
        title={
          <React.Fragment>
            <Avatar
              style={{ backgroundColor: '#' + group.color }}
              className={classes.groupAvatar}
              variant={'square'}
            >
              {' '}
            </Avatar>
            &nbsp;{group.name}
          </React.Fragment>
        }
        action={action}
        className={componentClasses.cardHeader}
      />
      <CardContent>
        <div className='information'>
          <span className='description group'>
            {group?.description ? `${group?.description}` : ''}
          </span>
          {group?.contact ? (
            <span className='contact group'>
              <span className='label'>Contact: </span>
              {group?.contact || 'No Contact provided'}
            </span>
          ) : null}
          <span className='owner group'>
            <span className='label'>Owner: </span>
            {group?.owner || 'No Owner provided'}
          </span>
        </div>

        <div>
          <Typography variant={'h6'}>Status</Typography>
          <StatusChip
            name={group.name || group.identifier}
            status={assessmentColor}
            value={field + ':' + message}
          />
        </div>

        <div className='labels'>{labels}</div>

        {links.length ? (
          <div className='linkContent'>
            <span className='linkLabel'>Links</span>
            <div className='links'>{links}</div>
          </div>
        ) : null}

        {items.length ? (
          <div className='itemsContent'>
            <Typography variant={'h6'}>Items</Typography>
            {items}
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
};

export default Group;
