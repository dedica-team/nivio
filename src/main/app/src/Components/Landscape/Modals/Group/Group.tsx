import React from 'react';

import { IAssessment, IGroup } from '../../../../interfaces';
import { getLabels, getLinks, getAssessmentSummary, getItemIcon } from '../../Utils/utils';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import StatusChip from '../../../StatusChip/StatusChip';
import componentStyles from '../../../../Ressources/styling/ComponentStyles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import Avatar from '@material-ui/core/Avatar';

interface Props {
  group: IGroup;
  assessments: IAssessment;
  locateItem?: (fullyQualifiedGroupIdentifier: string) => void;
  locateGroup?: (fullyQualifiedGroupIdentifier: string) => void;
}

/**
 * Returns a chosen Landscape group if informations are available
 */
const Group: React.FC<Props> = ({ group, assessments, locateItem, locateGroup }) => {
  const classes = componentStyles();

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
            <Avatar
              src={getItemIcon(item)}
              style={{ backgroundColor: 'white', border: '2px solid ' + status }}
            />
            &nbsp;
            {item.identifier}
          </Button>
        );
      });
    }
    return [];
  };
  const items = getGroupItems(group, locateItem);

  const [assessmentColor, message, field] = getAssessmentSummary(
    assessments.results[group.fullyQualifiedIdentifier]
  );
  const labels = getLabels(group);
  const links = getLinks(group);

  return (
    <Card className={classes.card}>
      <CardHeader
        title={'Group ' + group.name}
        onClick={() => {
          if (locateGroup) {
            locateGroup(group.fullyQualifiedIdentifier);
          }
        }}
        className={classes.cardHeader}
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

        {labels.length ? <div className='labels'>{labels}</div> : null}

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
