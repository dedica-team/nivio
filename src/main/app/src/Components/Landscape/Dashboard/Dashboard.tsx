import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';

import DashboardLayout from './DashboardLayout';
import { ILandscape, IAssessment, IItem } from '../../../interfaces';
import { get } from '../../../utils/API/APIClient';
import Group from '../Modals/Group/Group';
import Assessment from '../Modals/Assessment/Assessment';
import SearchResult from '../Search/SearchResult';

/**
 * Logic Component to display all available landscapes
 */
interface Props {
  setSidebarContent: Function;
  setFindFunction: Function;
}

const Dashboard: React.FC<Props> = ({ setSidebarContent }) => {
  const [landscape, setLandscape] = useState<ILandscape | null>();
  const [assessments, setAssessments] = useState<IAssessment | undefined>(undefined);
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);

  const findItem = (fullyQualifiedItemIdentifier: string) => {
    const element = document.getElementById(fullyQualifiedItemIdentifier);
    setHighlightElement(element);
  };

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
    setSidebarContent(
      <SearchResult useItem={item} findItem={findItem} onAssessmentClick={onItemAssessmentClick} />
    );
  };

  const onGroupClick = (fullyQualifiedGroupIdentifier: string) => {
    setSidebarContent(
      <Group
        fullyQualifiedGroupIdentifier={fullyQualifiedGroupIdentifier}
        findItem={findItem}
        findGroup={findGroup}
        onAssessmentClick={onGroupAssessmentClick}
      />
    );
  };

  const onGroupAssessmentClick = (fullyQualifiedGroupIdentifier: string) => {
    setSidebarContent(
      <Assessment
        fullyQualifiedIdentifier={fullyQualifiedGroupIdentifier}
        findItem={findItem}
        findGroup={findGroup}
        isGroup={true}
      />
    );
  };

  const onItemAssessmentClick = (fullyQualifiedItemIdentifier: string) => {
    setSidebarContent(
      <Assessment
        fullyQualifiedIdentifier={fullyQualifiedItemIdentifier}
        findItem={findItem}
        isGroup={false}
      />
    );
  };

  const { identifier } = useParams<{ identifier: string }>();

  useEffect(() => {
    get(`/api/${identifier}`).then((response) => {
      setLandscape(response);
    });

    get(`/assessment/${identifier}`).then((response) => {
      setAssessments(response);
    });
  }, [identifier]);

  return (
    <React.Fragment>
      <DashboardLayout
        landscape={landscape}
        assessments={assessments}
        onItemClick={onItemClick}
        onGroupClick={onGroupClick}
        onGroupAssessmentClick={onGroupAssessmentClick}
        onItemAssessmentClick={onItemAssessmentClick}
        findItem={findItem}
      />
    </React.Fragment>
  );
};

export default Dashboard;
