import React, { useEffect, useState, ReactElement } from 'react';
import { get } from '../../../utils/API/APIClient';
import './LandscapeGroup.scss';

import { IAssessmentProps, IGroup } from '../../../interfaces';
import { getAssessmentSummaryColorAndMessage } from '../../../utils/styling/style-helper';

interface Props {
  fullyQualifiedGroupIdentifier: string;
  findItem?: (fullyQualifiedGroupIdentifier: string) => void;
  findGroup?: (fullyQualifiedGroupIdentifier: string) => void;
  group?: IGroup;
  small?: boolean;
}

/**
 * Returns a choosen Landscape group if informations are available
 */
const LandscapeGroup: React.FC<Props> = ({
  fullyQualifiedGroupIdentifier,
  findItem,
  findGroup,
}) => {
  const [group, setGroup] = useState<IGroup | null>();

  const [assessment, setAssessment] = useState<IAssessmentProps[] | null>(null);

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

  const getGroupLabels = () => {
    let labels: ReactElement[] = [];
    if (group?.labels) {
      Object.keys(group.labels).forEach((key) => {
        if (group && group.labels && group.labels[key]) {
          if (!key.startsWith('icon') && !key.startsWith('status')) {
            const labelContent = (
              <span className='labelContent group' key={key}>
                <span className='label'>{key}: </span>
                {group.labels[key]}
              </span>
            );
            labels.push(labelContent);
          }
        }
      });
    }
    return labels;
  };

  const getGroupLinks = () => {
    let links: ReactElement[] = [];
    if (group?._links) {
      Object.keys(group._links).forEach((key) => {
        if (group && group._links && !key.startsWith('self')) {
          const linkContent = (
            <a
              href={group._links[key].href}
              target='_blank'
              rel='noopener noreferrer'
              className='link'
              key={key}
            >
              {key}
            </a>
          );
          links.push(linkContent);
        }
      });
    }
    return links;
  };

  const getGroupItems = () => {
    if (group?.items) {
      return group.items.map((item) => {
        return (
          <span
            className='item'
            key={item.fullyQualifiedIdentifier}
            onClick={() => {
              if (findItem) {
                findItem(item.fullyQualifiedIdentifier);
              }
            }}
          >
            {item.identifier}
          </span>
        );
      });
    }
    return [];
  };

  if (group) {
    const [assessmentColor] = getAssessmentSummaryColorAndMessage(assessment, group.identifier);
    const labels = getGroupLabels();
    const links = getGroupLinks();
    const items = getGroupItems();

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
          <span className='status' style={{ backgroundColor: assessmentColor }}></span>
        </div>
        <div className='information'>
          <span className='description group'>
            {group?.description ? `${group?.description}` : ''}
          </span>
          <span className='contact group'>
            <span className='label'>Contact: </span>
            {group?.contact || 'No Contact provided'}
          </span>
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

export default LandscapeGroup;
