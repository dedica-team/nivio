import React, { useState, useEffect, useCallback, ReactElement, MouseEvent } from 'react';
import { useParams } from 'react-router-dom';

import LandscapeDashboardLayout from './LandscapeDashboardLayout';
import Slider from '../../SliderComponent/Slider';
import { ILandscape, IItem, IAssesment } from '../../../interfaces';
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
  const [assesments, setAssesments] = useState<IAssesment | null>(null);

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
      }
    }
  }, [loadLandscape, landscape, landscapeIdentifier]);

  const getAllAssesments = useCallback(async () => {
    if (landscape) {
      setAssesments(await get(`/assessment/${landscape.identifier}`));
    }
  }, [landscape]);

  useEffect(() => {
    getLandscape();
    getAllAssesments();
  }, [getLandscape, getAllAssesments]);

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
        assesments={assesments}
        onItemClick={onItemClick}
      />
    </div>
  );
};

export default LandscapeDashboard;
