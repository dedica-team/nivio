import React, { ReactElement } from 'react';
import { IAssessmentProps, IGroup, IItem, ILandscape } from '../../../interfaces';
import { Button, Link, List, ListItem, ListItemText } from '@material-ui/core';

/**
 * Find an item by its fully qualified identifier.
 *
 * @param landscape object containing groups
 * @param fullyQualifiedIdentifier string to identify the item
 */
export const getItem = (landscape: ILandscape, fullyQualifiedIdentifier: string): IItem | null => {
  let item: IItem | null = null;
  for (const value of landscape.groups) {
    for (let i = 0; i < value.items.length; i++) {
      let value1 = value.items[i];
      if (value1.fullyQualifiedIdentifier === fullyQualifiedIdentifier) {
        item = value1;
        break;
      }
    }
  }

  return item;
};

/**
 * Find a group by its fully qualified identifier.
 *
 * @param landscape object
 * @param fullyQualifiedIdentifier string to identify the group
 */
export const getGroup = (
  landscape: ILandscape,
  fullyQualifiedIdentifier: string
): IGroup | null => {
  let group: IGroup | null = null;
  for (let i = 0; i < landscape.groups.length; i++) {
    let value = landscape.groups[i];
    if (value.fullyQualifiedIdentifier === fullyQualifiedIdentifier) {
      group = value;
      break;
    }
  }

  return group;
};

/**
 * Renders the links of a component as buttons.
 *
 * @param element item/group/landscape
 */
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
  if (!element?.labels) {
    return null;
  }
  Object.keys(element.labels).forEach((key) => {
    if (element && element.labels && element.labels[key]) {
      if (
        key.startsWith('icon') ||
        key.startsWith('fill') ||
        key.startsWith('tag') ||
        key.startsWith('framework') ||
        key.startsWith('network') ||
        key === 'color'
      )
        return;
      if (element.labels[key] === '*') return;

      labels.push(
        <ListItem key={key}>
          <ListItemText
            primary={key}
            secondary={element.labels[key].substr(0, 150)}
            title={element.labels[key]}
          />
        </ListItem>
      );
    }
  });
  if (labels.length === 0) {
    return null;
  }
  return <List dense={true}>{labels}</List>;
};

/**
 * Returns only the labels having the given prefix.
 * @param prefix the label prefix to filter for
 * @param element the component having labels
 */
export const getLabelsWithPrefix = (prefix: string, element: IGroup | IItem) => {
  let labels: ReactElement[] = [];
  if (!element?.labels) {
    return null;
  }
  Object.keys(element.labels).forEach((key) => {
    if (element && element.labels && element.labels[key]) {
      if (!key.startsWith(prefix)) return;
      const primary = key.replace(prefix + '.', '');
      labels.push(
        <ListItem key={key}>
          <ListItemText
            primary={primary}
            secondary={element.labels[key].substr(0, 150)}
            title={element.labels[key]}
          />
        </ListItem>
      );
    }
  });
  if (labels.length === 0) {
    return null;
  }
  return <List dense={true}>{labels}</List>;
};

/**
 * Returns the summary field from a subset of assessments.
 * @param assessmentResults
 * @todo refactor to return IAssessmentProps
 */
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

export const getItemIcon = (item: IItem) => {
  if (item.labels) {
    return item.labels['fill'] ? item.labels['fill'] : item.icon;
  }
  return item.icon;
};
