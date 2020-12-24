import React, { ReactElement } from 'react';
import { IAssessmentProps, IGroup, IItem } from '../../../interfaces';
import { Button, Link } from '@material-ui/core';

export const getLinks = (element: IGroup | IItem): ReactElement[] => {
  let links: ReactElement[] = [];
  if (element?._links) {
    Object.keys(element._links).forEach((key) => {
      if (element && element._links && !key.startsWith('self')) {
        links.push(
          <Button
            component={Link}
            key={key}
            target='_blank'
            rel='noopener noreferrer'
            href={element._links[key].href}
          >
            {key}
          </Button>
        );
      }
    });
  }
  return links;
};

export const getLabels = (element: IGroup | IItem) => {
  let labels: ReactElement[] = [];
  if (element?.labels) {
    Object.keys(element.labels).forEach((key) => {
      if (element && element.labels && element.labels[key]) {
        if (key.startsWith('icon') || key.startsWith('fill')) return;

        labels.push(
          <div key={key}>
            <span className='labelContent' key={key}>
              {key}
            </span>
            : <strong>{element.labels[key]}</strong>
          </div>
        );
      }
    });
  }
  return labels;
};

export const getRelations = (
  item: IItem,
  findItem?: (fullyQualifiedItemIdentifier: string) => void
) => {
  if (item.relations && item.relations.length) {
    return item.relations.map((relation) => {
      let relationName: string;
      let groupNameStart: number;
      if (relation.target.endsWith(item.identifier)) {
        groupNameStart = relation.source.indexOf('/') + 1;
        relationName = relation.source.substr(groupNameStart);
        return (
          <span
            className='relation'
            key={relation.source}
            onClick={() => {
              if (findItem) {
                findItem(relation.source);
              }
            }}
          >
            {relationName}
          </span>
        );
      }
      groupNameStart = relation.target.indexOf('/') + 1;
      relationName = relation.target.substr(groupNameStart);
      return (
        <span
          className='relation'
          key={relation.target}
          onClick={() => {
            if (findItem) {
              findItem(relation.target);
            }
          }}
        >
          {relationName}
        </span>
      );
    });
  }
  return [];
};



export const getAssessmentSummary = (
  assessmentResults: IAssessmentProps[] | undefined
): string[] => {
  let assessmentColor = 'grey';
  let assessmentMessage = '';
  let assessmentField = '';

  if (assessmentResults) {
    const result = assessmentResults.find((assessmentResult) => assessmentResult.summary);

    if (result) {
      if (result.status !== 'UNKNOWN') {
        assessmentColor = result.status;
        assessmentField = result.maxField || '';
        assessmentMessage = result.message;
      } else {
        assessmentMessage = 'unknown status';
      }
    }
  }

  return [assessmentColor, assessmentMessage, assessmentField];
};

export const getAssessmentColor = (assessmentResults: IAssessmentProps): string => {
  let assessmentColor = 'grey';

  if (assessmentResults.status !== 'UNKNOWN') {
    assessmentColor = assessmentResults.status;
  }

  return assessmentColor;
};

export const getItemIcon = (item: IItem) => {
  if (item.labels) {
    return item.labels['fill'] ? item.labels['fill'] : item.icon;
  }
  return item.icon;
};
