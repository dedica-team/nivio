import React, { useEffect, useState, ReactElement } from 'react';
import { get } from '../../../../utils/API/APIClient';
import './Item.scss';

import { IItem, IAssessmentProps } from '../../../../interfaces';
import { getLabels, getLinks, getRelations, getAssessmentSummary } from '../../Utils/utils';

interface Props {
  fullyQualifiedItemIdentifier: string;
  findItem?: (fullyQualifiedItemIdentifier: string) => void;
  onAssessmentClick?: (fullyQualifiedItemIdentifier: string) => void;
  item?: IItem;
  small?: boolean;
}

/**
 * Returns a choosen Landscape Item if informations are available
 */
const Item: React.FC<Props> = ({ fullyQualifiedItemIdentifier, findItem, onAssessmentClick }) => {
  const [item, setItem] = useState<IItem | undefined>();
  const [assessment, setAssessment] = useState<IAssessmentProps[] | undefined>(undefined);

  useEffect(() => {
    get(`/api/${fullyQualifiedItemIdentifier}`).then((item) => {
      setItem(item);
    });

    const landscapeIdentifier = fullyQualifiedItemIdentifier.split('/');
    if (landscapeIdentifier[0]) {
      get(`/assessment/${landscapeIdentifier[0]}`).then((response) => {
        if (response) {
          setAssessment(response.results[fullyQualifiedItemIdentifier]);
        }
      });
    }
  }, [fullyQualifiedItemIdentifier]);

  if (item) {
    const [assessmentColor] = getAssessmentSummary(assessment);
    const labels: ReactElement[] = getLabels(item);
    const links: ReactElement[] = getLinks(item);
    const relations: ReactElement[] = getRelations(item, findItem);

    return (
      <div className='itemContent'>
        <div className='header'>
          <img src={item?.icon} alt='Icon' className='icon' />
          <span
            className='title'
            onClick={() => {
              if (findItem) {
                findItem(item.fullyQualifiedIdentifier);
              }
            }}
          >
            {item ? item.name || item.identifier : null}
          </span>
          <span
            className='status'
            style={{ backgroundColor: assessmentColor }}
            onClick={() => {
              if (onAssessmentClick) {
                onAssessmentClick(item.fullyQualifiedIdentifier);
              }
            }}
          ></span>
        </div>
        <div className='information'>
          <span className='description item'>
            {item?.description ? `${item?.description}` : ''}
          </span>
          <span className='contact item'>
            <span className='label'>Contact: </span>
            {item?.contact || 'No Contact provided'}
          </span>
          <span className='owner item'>
            <span className='label'>Owner: </span>
            {item?.owner || 'No Owner provided'}
          </span>
        </div>

        {labels.length ? <div className='labels'>{labels}</div> : null}

        {links.length ? (
          <div className='linkContent'>
            <span className='linkLabel'>Links</span>
            <div className='links'>{links}</div>
          </div>
        ) : null}

        {relations.length ? (
          <div className='relationsContent'>
            <span className='relationsLabel'>Relations</span>
            <div className='relations'>{relations}</div>
          </div>
        ) : null}
      </div>
    );
  }
  return (
    <div className='itemError'>
      <span className='errorMessage'>Error Loading Item</span>
      <span className='errorIdentifier'>{fullyQualifiedItemIdentifier} does not exist!</span>
    </div>
  );
};

export default Item;
