import React, { useEffect, useState, ReactElement } from 'react';
import { get } from '../../../utils/API/APIClient';
import './LandscapeItem.scss';

import { IItem, IAssessmentProps } from '../../../interfaces';
import { getAssessmentColorAndMessage } from '../../../utils/styling/style-helper';

interface Props {
  fullyQualifiedItemIdentifier: string;
  findItem?: (x: number, y: number) => void;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 */
const LandscapeItem: React.FC<Props> = ({ fullyQualifiedItemIdentifier, findItem }) => {
  const [item, setItem] = useState<IItem | null>();
  const [assessment, setAssessment] = useState<IAssessmentProps[] | null>(null);

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

  let assesmentColor = 'grey';
  let labels: ReactElement[] = [];
  let links: ReactElement[] = [];
  let relations: ReactElement[] = [];
  let x: number;
  let y: number;

  if (item) {
    [assesmentColor] = getAssessmentColorAndMessage(assessment, item.identifier);

    if (item.labels) {
      Object.keys(item.labels).forEach((key) => {
        if (item && item.labels && item.labels[key]) {
          if (!key.startsWith('nivio') && !key.startsWith('icon') && !key.startsWith('status')) {
            const labelContent = (
              <span className='labelContent item' key={key}>
                <span className='label'>{key}: </span>
                {item.labels[key]}
              </span>
            );
            labels.push(labelContent);
          }
        }
      });

      if (item.labels['nivio.rendered.x'] && item.labels['nivio.rendered.y']) {
        x = parseInt(item.labels['nivio.rendered.x']);
        y = parseInt(item.labels['nivio.rendered.y']);
      }
    }

    if (item._links) {
      Object.keys(item._links).forEach((key) => {
        if (item && item._links) {
          const linkContent = (
            <a
              href={item._links[key].href}
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

    if (item.relations && item.relations.length) {
      relations = item.relations.map((relation) => {
        if (relation.target.endsWith(item.identifier)) {
          const relationTarget = relation.source.split('/').pop();
          return (
            <span className='relation' key={relation.source}>
              {relationTarget}
            </span>
          );
        }
        const relationTarget2 = relation.target.split('/').pop();
        return (
          <span className='relation' key={relation.target}>
            {relationTarget2}
          </span>
        );
      });
    }
  }

  return (
    <div className='itemContent'>
      <div className='header'>
        <img src={item?.labels?.['nivio.rendered.icon']} alt='Item Icon' className='icon'></img>
        <span
          className='title'
          onClick={(e) => {
            if (findItem && x && y) {
              findItem(x, y);
            }
          }}
        >
          {item ? item.name || item.identifier : null}
        </span>
        <span className='status' style={{ backgroundColor: assesmentColor }}></span>
      </div>
      <div className='information'>
        <span className='description item'>{item?.description ? `${item?.description}` : ''}</span>
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
};

export default LandscapeItem;
