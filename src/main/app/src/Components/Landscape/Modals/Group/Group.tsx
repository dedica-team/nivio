import React from 'react';

import { IAssessment, IAssessmentProps, IGroup } from '../../../../interfaces';
import { getLabels, getLinks, getGroupItems, getAssessmentSummary } from '../../Utils/utils';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import StatusChip from '../../../StatusChip/StatusChip';

interface Props {
  group: IGroup;
  assessments: IAssessment;
  findItem?: (fullyQualifiedGroupIdentifier: string) => void;
  findGroup?: (fullyQualifiedGroupIdentifier: string) => void;
}

/**
 * Returns a choosen Landscape group if informations are available
 */
const Group: React.FC<Props> = ({ group, assessments, findItem, findGroup }) => {
  if (group) {
    const [assessmentColor] = getAssessmentSummary(
      assessments.results[group.fullyQualifiedIdentifier]
    );
    const labels = getLabels(group);
    const links = getLinks(group);
    const items = getGroupItems(group, findItem);

    return (
      <Card>
        <CardHeader
          title={group.name}
          onClick={() => {
            if (findGroup) {
              findGroup(group.fullyQualifiedIdentifier);
            }
          }}
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
          <StatusChip name={group.name || group.identifier} status={assessmentColor} />

          {labels.length ? <div className='labels'>{labels}</div> : null}

          {links.length ? (
            <div className='linkContent'>
              <span className='linkLabel'>Links</span>
              <div className='links'>{links}</div>
            </div>
          ) : null}

          {items.length ? (
            <div className='itemsContent'>
              <span className='itemsLabel'>Items</span>
              <div className='items'>{items}</div>
            </div>
          ) : null}
        </CardContent>
      </Card>
    );
  }

  return (
    <div className='groupError'>
      <span className='errorMessage'>Error Loading Group</span>
    </div>
  );
};

export default Group;
