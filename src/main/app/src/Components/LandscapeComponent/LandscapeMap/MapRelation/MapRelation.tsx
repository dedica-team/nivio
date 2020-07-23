import React from 'react';
import './MapRelation.scss';

interface Props {
  sourceIdentifier: string;
  targetIdentifier: string;
  type: string | null;
  findItem?: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a choosen Map Relation
 * @param element Choosen SVG Element from our Landscape Component
 */
const MapRelation: React.FC<Props> = ({ sourceIdentifier, targetIdentifier, type, findItem }) => {
  return (
    <div className='mapRelationContent'>
      <span className='type'>Type: {type}</span>
      <div className='relationsContent'>
        <span
          className='relation'
          key={sourceIdentifier}
          onClick={() => {
            if (findItem) {
              findItem(sourceIdentifier);
            }
          }}
        >
          {sourceIdentifier}
        </span>
        <span
          className='relation'
          key={targetIdentifier}
          onClick={() => {
            if (findItem) {
              findItem(targetIdentifier);
            }
          }}
        >
          {targetIdentifier}
        </span>
      </div>
    </div>
  );
};

export default MapRelation;
