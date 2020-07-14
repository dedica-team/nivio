import React, { useState, useEffect, useCallback, ReactElement, MouseEvent } from 'react';
import { useParams } from 'react-router-dom';

import LandscapeDashboardLayout from './LandscapeDashboardLayout';
import Slider from '../../SliderComponent/Slider';
import { ILandscape, IItem, IAssessment } from '../../../interfaces';
import { get } from '../../../utils/API/APIClient';
import LandscapeItem from '../LandscapeItem/LandscapeItem';
import { CSSTransition } from 'react-transition-group';

/**
 * Logic Component to display all available landscapes
 */

const LandscapeDashboard: React.FC = () => {
  const [landscape, setLandscape] = useState<ILandscape | null>();
  const [loadLandscape, setLoadLandscape] = useState<boolean>(true);
  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
  const [cssAnimationKey, setCssAnimationKey] = useState('');
  const [assessments, setAssessments] = useState<IAssessment | null>(null);
  const [loadAssessments, setLoadAssessments] = useState<boolean>(false);

  const onItemClick = (e: MouseEvent<HTMLSpanElement>, item: IItem) => {
    setSliderContent(
      <LandscapeItem fullyQualifiedItemIdentifier={item.fullyQualifiedIdentifier} />
    );
    setCssAnimationKey(e.currentTarget.id);
    setShowSlider(true);
  };

  const closeSlider = () => {
    setShowSlider(false);
  };

  const { landscapeIdentifier } = useParams();

  const getLandscape = useCallback(async () => {
    if (loadLandscape) {
      setLandscape(await get(`/api/${landscapeIdentifier}`));
      if (landscape) {
        setLoadLandscape(false);
        setLoadAssessments(true);
      }
    }
  }, [loadLandscape, landscape, landscapeIdentifier]);

  const getAllAssessments = useCallback(async () => {
    if (loadAssessments && landscape) {
      setAssessments(await get(`/assessment/${landscape.identifier}`));
      setLoadAssessments(false);
    }
  }, [landscape, loadAssessments]);

  useEffect(() => {
    getLandscape();
    getAllAssessments();
  }, [getLandscape, getAllAssessments]);

  return (
    <div className='landscapeContainer'>
      <CSSTransition
        key={cssAnimationKey}
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
      />
    </div>
  );
};

export default LandscapeDashboard;
