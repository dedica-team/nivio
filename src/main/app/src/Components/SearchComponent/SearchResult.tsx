import React, { useState, ReactElement, useEffect } from 'react';
import { Card } from '@material-ui/core';
import { get } from '../../utils/API/APIClient';
import CardContent from '@material-ui/core/CardContent';
import CardActions from '@material-ui/core/CardActions';
import { IAssessmentProps, IItem } from '../../interfaces';
import { getAssessmentColorAndMessage } from '../../utils/styling/style-helper';
import Button from '@material-ui/core/Button';

import './Search.scss';

interface Props {
  item: IItem;
  findItem?: (fullyQualifiedItemIdentifier: string) => void;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 */
const SearchResult: React.FC<Props> = ({ item, findItem }) => {
  const [assessment, setAssessment] = useState<IAssessmentProps[] | null>(null);

  let assesmentColor = 'grey';
  let relations: ReactElement[] = [];

  useEffect(() => {
    const landscapeIdentifier = item.fullyQualifiedIdentifier.split('/');
    if (landscapeIdentifier[0]) {
      get(`/assessment/${landscapeIdentifier[0]}`).then((response) => {
        if (response) {
          setAssessment(response.results[item.fullyQualifiedIdentifier]);
        }
      });
    }
  }, [item]);

  if (item) {
    [assesmentColor] = getAssessmentColorAndMessage(assessment, item.identifier);
  }

  if (item?.relations && item.relations.length) {
    relations = item.relations.map((relation) => {
      let relationName: string;
      let groupNameStart: number;
      if (relation.target.endsWith(item.identifier)) {
        groupNameStart = relation.source.indexOf('/') + 1;
        relationName = relation.source.substr(groupNameStart);
        return (
          <Button
            size={'small'}
            variant={'outlined'}
            className='relation'
            key={relation.source}
            onClick={() => {
              if (findItem) {
                findItem(relation.source);
              }
            }}
          >
            {relationName}
          </Button>
        );
      }
      groupNameStart = relation.target.indexOf('/') + 1;
      relationName = relation.target.substr(groupNameStart);
      return (
        <Button
          size={'small'}
          variant={'outlined'}
          className='relation'
          key={relation.target}
          onClick={() => {
            if (findItem) {
              findItem(relation.target);
            }
          }}
        >
          {relationName}
        </Button>
      );
    });
  }

  return (
    <Card className={'searchResult'} square={true}>
      <CardContent>
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
          <span className='status' style={{ backgroundColor: assesmentColor }}></span>
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
      </CardContent>
      <CardActions>
        {relations.length ? (
          <div className='relationsContent'>
            <span className='relationsLabel'>Relations</span>
            <div className='relations'>{relations}</div>
          </div>
        ) : null}
      </CardActions>
    </Card>
  );
};
export default SearchResult;
