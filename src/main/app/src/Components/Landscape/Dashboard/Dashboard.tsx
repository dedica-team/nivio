import React, { useState, useEffect } from 'react';

import DashboardLayout from './DashboardLayout';
import {ILandscape, IAssessment, IItem, IGroup} from '../../../interfaces';
import Group from '../Modals/Group/Group';
import SearchResult from '../Search/SearchResult';

/**
 * Logic Component to display all status of groups and items.
 */
interface Props {
  setSidebarContent: Function;
  findItem: (fqi: string) => void;
  landscape: ILandscape;
  assessments: IAssessment;
}

const Dashboard: React.FC<Props> = ({ setSidebarContent, findItem, landscape, assessments }) => {
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
    findItem(item.fullyQualifiedIdentifier);
    setSidebarContent(<SearchResult useItem={item} findItem={findItem} />);
  };

  const onGroupClick = (group: IGroup) => {
    setSidebarContent(
        <Group
          group={group}
          assessments={assessments}
          findItem={findItem}
          findGroup={findGroup}
        />
    );
  };

  if (landscape && assessments)
    return (
      <DashboardLayout
        landscape={landscape}
        assessments={assessments}
        onItemClick={onItemClick}
        onGroupClick={onGroupClick}
      />
    );

  return null;
};

export default Dashboard;
