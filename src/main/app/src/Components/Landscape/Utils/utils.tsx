import React, { ReactElement } from 'react';
import { IGroup, IItem, IAssessmentProps } from '../../../interfaces';

export const getLinks = (element: IGroup | IItem) => {
  let links: ReactElement[] = [];
  if (element?._links) {
    Object.keys(element._links).forEach((key) => {
      if (element && element._links && !key.startsWith('self')) {
        const linkContent = (
          <a
            href={element._links[key].href}
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

export const getLabels = (element: IGroup | IItem) => {
  let labels: ReactElement[] = [];
  if (element?.labels) {
    Object.keys(element.labels).forEach((key) => {
      if (element && element.labels && element.labels[key]) {
        if (!key.startsWith('icon') && !key.startsWith('status') && !key.startsWith('fill')) {
          const labelContent = (
            <span className='labelContent' key={key}>
              <span className='label'>{key}: </span>
              {element.labels[key]}
            </span>
          );
          labels.push(labelContent);
        }
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

export const getGroupItems = (
  group: IGroup,
  findItem?: (fullyQualifiedItemIdentifier: string) => void
) => {
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

export const getAssessmentSummary = (
  assessmentResults: IAssessmentProps[] | undefined
): string[] => {
  let assessmentColor = 'grey';
  let assessmentMessage = '';
  let assessmentField = '';

  if (assessmentResults) {
    const result = assessmentResults.find((assessmentResult) => assessmentResult.summary === true);

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
  return item.labels['fill'] ? item.labels['fill'] : item?.icon;
};
