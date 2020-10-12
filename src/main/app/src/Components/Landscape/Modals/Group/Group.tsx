import React, { useEffect, useState } from 'react';
import { get } from '../../../../utils/API/APIClient';
import './Group.scss';

import { IAssessmentProps, IGroup } from '../../../../interfaces';
import { getLabels, getLinks, getGroupItems, getAssessmentSummary } from '../../Utils/utils';

interface Props {
  fullyQualifiedGroupIdentifier: string;
  findItem?: (fullyQualifiedGroupIdentifier: string) => void;
  findGroup?: (fullyQualifiedGroupIdentifier: string) => void;
  onAssessmentClick?: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a choosen Landscape group if informations are available
 */
const Group: React.FC<Props> = ({
  fullyQualifiedGroupIdentifier,
  findItem,
  findGroup,
  onAssessmentClick,
}) => {
  const [group, setGroup] = useState<IGroup | undefined>();
  const [assessment, setAssessment] = useState<IAssessmentProps[] | undefined>(undefined);

  useEffect(() => {
    get(`/api/${fullyQualifiedGroupIdentifier}`).then((group) => {
      setGroup(group);
    });

    const landscapeIdentifier = fullyQualifiedGroupIdentifier.split('/');
    if (landscapeIdentifier[0]) {
      get(`/assessment/${landscapeIdentifier[0]}`).then((response) => {
        if (response) {
          setAssessment(response.results[fullyQualifiedGroupIdentifier]);
        }
      });
    }
  }, [fullyQualifiedGroupIdentifier]);

  if (group) {
    const [assessmentColor] = getAssessmentSummary(assessment);
    const labels = getLabels(group);
    const links = getLinks(group);
    const items = getGroupItems(group, findItem);

    return (
      <div className='groupContent'>
        <div className='header'>
          <span
            className='title'
            onClick={() => {
              if (findGroup) {
                findGroup(group.fullyQualifiedIdentifier);
              }
            }}
          >
            {group ? group.name || group.identifier : null}
          </span>
          <span
            className='status'
            style={{ backgroundColor: assessmentColor }}
            onClick={() => {
              if (onAssessmentClick) {
                onAssessmentClick(group.fullyQualifiedIdentifier);
              }
            }}
          ></span>
        </div>
        <div className='information'>
          <span className='description group'>
            {group?.description ? `${group?.description}` : ''}
          </span>
          { group?.contact ? (<span className='contact group'>
            <span className='label'>Contact: </span>
            {group?.contact || 'No Contact provided'}
          </span>) : null}
          <span className='owner group'>
            <span className='label'>Owner: </span>
            {group?.owner || 'No Owner provided'}
          </span>
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
            <span className='itemsLabel'>Items</span>
            <div className='items'>{items}</div>
          </div>
        ) : null}
      </div>
    );
  }

  return (
    <div className='groupError'>
      <span className='errorMessage'>Error Loading Group</span>
      <span className='errorIdentifier'>{fullyQualifiedGroupIdentifier} does not exist!</span>
    </div>
  );
};

export default Group;
