import React, { useState, ReactElement, MouseEvent } from 'react';

import { useParams } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import LandscapeItem from '../LandscapeItem/LandscapeItem';
import LandscapeMap from './Map/LandscapeMap';
import Slider from '../../SliderComponent/Slider';

/**
 * Logic component for your landscape map
 */
const Landscape: React.FC = () => {
  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
  const [cssAnimationKey, setCssAnimationKey] = useState('');

  const { identifier } = useParams();

  const onItemClick = (e: MouseEvent<HTMLElement>) => {
    const fullyQualifiedItemIdentifier = e.currentTarget.getAttribute('data-identifier');
    if (fullyQualifiedItemIdentifier) {
      setSliderContent(
        <LandscapeItem fullyQualifiedItemIdentifier={fullyQualifiedItemIdentifier} />
      );
      setCssAnimationKey(e.currentTarget.id);
      setShowSlider(true);
    }
  };

  const closeSlider = () => {
    setShowSlider(false);
  };

  if (identifier) {
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
        <LandscapeMap identifier={identifier} onItemClick={onItemClick} />
      </div>
    );
  }

  return (
    <div className='landscapeError'>
      <span className='error'>Loading...</span>
    </div>
  );
};

export default Landscape;
