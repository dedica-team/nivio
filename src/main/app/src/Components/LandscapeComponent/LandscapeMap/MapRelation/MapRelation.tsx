import React from 'react';
import './MapRelation.scss';

interface Props {
  sourceIdentifier: string;
  targetIdentifier: string;
  type: string | null;
  findItem: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a choosen Map Relation
 * @param element Choosen SVG Element from our Landscape Component
 */
const MapRelation: React.FC<Props> = ({ sourceIdentifier, targetIdentifier, type, findItem }) => {
  const sourceGroupNameStart = sourceIdentifier.indexOf('/') + 1;
  const sourceRelation = sourceIdentifier.substr(sourceGroupNameStart);

  const targetGroupNameStart = targetIdentifier.indexOf('/') + 1;
  const targetRelation = targetIdentifier.substr(targetGroupNameStart);

  const sourceTitle = sourceIdentifier.split('/').pop();
  const targetTitle = targetIdentifier.split('/').pop();

  return (
    <div className='mapRelation'>
      <div className='titleContainer'>
        <span className='title'>
          {sourceTitle} {'⇄'} {targetTitle}
        </span>
      </div>
      <div className='mapRelationContent'>
        <span className='type'>Type: {type}</span>
        <div className='relationsContent'>
          <span
            className='relation'
            key={sourceIdentifier}
            onClick={() => {
              findItem(sourceIdentifier);
            }}
          >
            {sourceRelation}
          </span>
          <span
            className='relation'
            key={targetIdentifier}
            onClick={() => {
              findItem(targetIdentifier);
            }}
          >
            {targetRelation}
          </span>
        </div>
      </div>
    </div>
  );
};

export default MapRelation;
