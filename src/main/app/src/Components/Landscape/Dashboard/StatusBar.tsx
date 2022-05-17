import React, { useContext, useEffect, useState } from 'react';

import StatusBarLayout from './StatusBarLayout';
import { IAssessment, IGroup, IItem, ILandscape } from '../../../interfaces';
import Group from '../Modals/Group/Group';
import Item from '../Modals/Item/Item';
import { LocateFunctionContext } from '../../../Context/LocateFunctionContext';

/**
 * Logic Component to display all status of groups and items.
 */
interface Props {
  setSidebarContent: Function;
  landscape: ILandscape;
  assessments: IAssessment;
}

const StatusBar: React.FC<Props> = ({ setSidebarContent, landscape, assessments }) => {
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);

  const locateFunctionContext = useContext(LocateFunctionContext);

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
    locateFunctionContext.locateFunction(item.fullyQualifiedIdentifier);
    setSidebarContent(
      <Item fullyQualifiedItemIdentifier={item.fullyQualifiedIdentifier} sticky={true} />
    );
  };

  const onGroupClick = (group: IGroup) => {
    findGroup(group.fullyQualifiedIdentifier);
    setSidebarContent(<Group defaultGroup={group} sticky={true} />);
  };

  if (landscape && assessments)
    return <StatusBarLayout onItemClick={onItemClick} onGroupClick={onGroupClick} />;

  return null;
};

export default StatusBar;
