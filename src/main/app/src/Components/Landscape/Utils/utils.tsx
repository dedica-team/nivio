import React, {ReactElement} from 'react';
import {IGroup, IItem, ILandscape, IRelation} from '../../../interfaces';
import {Button, Link, List, ListItem, ListItemText} from '@material-ui/core';
import MappedString from './MappedString';

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

export const getLabels = (element: IGroup | IItem | IRelation) => {
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

export const getMappedLabels = (element: IGroup | IItem | IRelation) => {
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
            primary={<MappedString mapKey={key} />}
            secondary={<MappedString mapKey={element.labels[key]} />}
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
  if (!element || !element?.labels) {
    return null;
  }
  let labels: ReactElement[] = [];
  const strings = Object.keys(element.labels);
  strings
    .filter((key) => key.startsWith(prefix))
    .forEach((key) => {
      const value = element.labels?.[key] || null;
      if (!value) return;
      const primary = key.replace(prefix + '.', '');
      const secondary = value.substr(0, 150);
      labels.push(
        <ListItem key={key}>
          <ListItemText primary={primary} secondary={secondary} title={value} />
        </ListItem>
      );
    });
  if (labels.length === 0) {
    return null;
  }
  return <List dense={true}>{labels}</List>;
};

export const getItemIcon = (item: IItem) => {
  if (item.labels) {
    return item.labels['fill'] ? item.labels['fill'] : item.icon;
  }
  return item.icon;
};