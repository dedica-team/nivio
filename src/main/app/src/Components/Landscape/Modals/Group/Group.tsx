import React, { useContext } from 'react';

import { IAssessment, IGroup } from '../../../../interfaces';
import { getLabels, getLinks, getAssessmentSummary } from '../../Utils/utils';
import { Card, CardHeader, Theme } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import StatusChip from '../../../StatusChip/StatusChip';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import IconButton from '@material-ui/core/IconButton';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import ItemAvatar from "../Item/ItemAvatar";

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    groupAvatar: {
      width: 15,
      height: 15,
      display: 'inline-block',
    },
  })
);

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
            <ItemAvatar item={item} statusColor={status} />
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

  return (
    <Card className={componentClasses.card}>
      <CardHeader
        title={
          <React.Fragment>
            <IconButton
              onClick={() => locateFunctionContext.locateFunction(group.fullyQualifiedIdentifier)}
              size={'small'}
            >
              <Avatar
                className={classes.groupAvatar}
                title={'Click to highlight the group.'}
                style={{
                  backgroundColor: 'rgba(255, 255, 255, 0.75)',
                  border: '2px solid #' + group.color,
                  height: '2rem',
                  width: '2rem'
                }}
              >
                {' '}
              </Avatar>
            </IconButton>

            &nbsp;{group.name}
          </React.Fragment>
        }
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
