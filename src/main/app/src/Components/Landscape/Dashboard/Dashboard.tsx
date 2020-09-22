import React, { useState, useEffect, ReactElement } from 'react';
import { useParams } from 'react-router-dom';

import DashboardLayout from './DashboardLayout';
import Slider from '../../Slider/Slider';
import { ILandscape, IAssessment } from '../../../interfaces';
import { get } from '../../../utils/API/APIClient';
import Item from '../Modals/Item/Item';
import Group from '../Modals/Group/Group';
import { CSSTransition } from 'react-transition-group';
import Assessment from '../Modals/Assessment/Assessment';

/**
 * Logic Component to display all available landscapes
 */

const Dashboard: React.FC = () => {
  const [landscape, setLandscape] = useState<ILandscape | null>();
  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
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

  const onItemClick = (fullyQualifiedItemIdentifier: string) => {
    setSliderContent(
      <Item
        fullyQualifiedItemIdentifier={fullyQualifiedItemIdentifier}
        findItem={findItem}
        onAssessmentClick={onItemAssessmentClick}
      />
    );
    setShowSlider(true);
  };

  const onGroupClick = (fullyQualifiedGroupIdentifier: string) => {
    setSliderContent(
      <Group
        fullyQualifiedGroupIdentifier={fullyQualifiedGroupIdentifier}
        findItem={findItem}
        findGroup={findGroup}
        onAssessmentClick={onGroupAssessmentClick}
      />
    );
    setShowSlider(true);
  };

  const onGroupAssessmentClick = (fullyQualifiedGroupIdentifier: string) => {
    setSliderContent(
      <Assessment
        fullyQualifiedIdentifier={fullyQualifiedGroupIdentifier}
        findItem={findItem}
        findGroup={findGroup}
        isGroup={true}
      />
    );
    setShowSlider(true);
  };

  const onItemAssessmentClick = (fullyQualifiedItemIdentifier: string) => {
    setSliderContent(
      <Assessment
        fullyQualifiedIdentifier={fullyQualifiedItemIdentifier}
        findItem={findItem}
        isGroup={false}
      />
    );
    setShowSlider(true);
  };

  const closeSlider = () => {
    setShowSlider(false);
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
      <CSSTransition
        in={showSlider}
        timeout={{ enter: 0, exit: 1000, appear: 1000 }}
        appear
        unmountOnExit
        classNames='slider'
      >
        <Slider sliderContent={sliderContent} closeSlider={closeSlider} />
      </CSSTransition>
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
