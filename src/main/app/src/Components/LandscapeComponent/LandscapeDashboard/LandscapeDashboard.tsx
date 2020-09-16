import React, { useState, useEffect, ReactElement, MouseEvent } from 'react';
import { useParams } from 'react-router-dom';

import LandscapeDashboardLayout from './LandscapeDashboardLayout';
import Slider from '../../SliderComponent/Slider';
import { ILandscape, IItem, IAssessment, IGroup } from '../../../interfaces';
import { get } from '../../../utils/API/APIClient';
import LandscapeItem from '../LandscapeItem/LandscapeItem';
import LandscapeGroup from '../LandscapeGroup/LandscapeGroup';
import { CSSTransition } from 'react-transition-group';
import LandscapeAssessment from '../LandscapeAssessment/LandscapeAssessment';

/**
 * Logic Component to display all available landscapes
 */

const LandscapeDashboard: React.FC = () => {
  const [landscape, setLandscape] = useState<ILandscape | null>();
  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
  const [assessments, setAssessments] = useState<IAssessment | null>(null);
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

  const onItemClick = (e: MouseEvent<HTMLSpanElement>, item: IItem) => {
    setSliderContent(
      <LandscapeItem
        fullyQualifiedItemIdentifier={item.fullyQualifiedIdentifier}
        findItem={findItem}
      />
    );
    setShowSlider(true);
  };

  const onGroupClick = (e: MouseEvent<HTMLSpanElement>, group: IGroup) => {
    setSliderContent(
      <LandscapeGroup
        fullyQualifiedGroupIdentifier={group.fullyQualifiedIdentifier}
        findItem={findItem}
        findGroup={findGroup}
      />
    );
    setShowSlider(true);
  };

  const onGroupAssessmentClick = (e: MouseEvent<HTMLSpanElement>, group: IGroup) => {
    setSliderContent(
      <LandscapeAssessment
        fullyQualifiedIdentifier={group.fullyQualifiedIdentifier}
        findItem={findItem}
        findGroup={findGroup}
        isGroup={true}
      />
    );
    setShowSlider(true);
  };

  const onItemAssessmentClick = (e: MouseEvent<HTMLSpanElement>, item: IItem) => {
    setSliderContent(
      <LandscapeAssessment
        fullyQualifiedIdentifier={item.fullyQualifiedIdentifier}
        findGroup={findGroup}
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
      <LandscapeDashboardLayout
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

export default LandscapeDashboard;
