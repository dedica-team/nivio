import React, { useState, useEffect } from 'react';

import StatusBarLayout from './StatusBarLayout';
import { ILandscape, IAssessment, IItem, IGroup } from '../../../interfaces';
import Group from '../Modals/Group/Group';
import Item from '../Modals/Item/Item';

/**
 * Logic Component to display all status of groups and items.
 */
interface Props {
  setSidebarContent: Function;
  locateItem: (fqi: string) => void;
  landscape: ILandscape;
  assessments: IAssessment;
}

const StatusBar: React.FC<Props> = ({ setSidebarContent, locateItem, landscape, assessments }) => {
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);

  const findGroup = (fullyQualifiedGroupIdentifier: string) => {
    const element = document.getElementById(fullyQualifiedGroupIdentifier);
    setHighlightElement(element);
  };

  useEffect(() => {
    let timeout: NodeJS.Timeout;

    if (highlightElement instanceof Element) {
      highlightElement.classList.add('highlightDot');
      highlightElement.scrollIntoView({ behavior: 'smooth', block: 'end' });
      const itemGroup = highlightElement.parentElement?.parentElement?.parentElement;
      itemGroup?.classList.add('highlightGroup');

      timeout = setTimeout(() => {
        highlightElement.classList.remove('highlightDot');
        itemGroup?.classList.remove('highlightGroup');
        setHighlightElement(null);
      }, 2000);
    }

    return () => clearTimeout(timeout);
  }, [highlightElement]);

  const onItemClick = (item: IItem) => {
    locateItem(item.fullyQualifiedIdentifier);
    setSidebarContent(<Item useItem={item} locateItem={locateItem} />);
  };

  const onGroupClick = (group: IGroup) => {
    findGroup(group.fullyQualifiedIdentifier);
    setSidebarContent(
      <Group group={group} assessments={assessments} locateItem={locateItem} locateGroup={findGroup} />
    );
  };

  if (landscape && assessments)
    return (
      <StatusBarLayout
        landscape={landscape}
        assessments={assessments}
        onItemClick={onItemClick}
        onGroupClick={onGroupClick}
      />
    );

  return null;
};

export default StatusBar;
