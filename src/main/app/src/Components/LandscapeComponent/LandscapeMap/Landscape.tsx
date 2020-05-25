import React, { useState, ReactElement } from 'react';

import { useParams } from 'react-router-dom';
import { CSSTransition } from 'react-transition-group';

import LandscapeItem from '../Item/LandscapeItem';
import LandscapeMap from './Map/LandscapeMap';
import LandscapeSlider from './Slider/LandscapeSlider';

/**
 * Displays a choosen landscape as interactive SVG
 */
const Landscape: React.FC = () => {
  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
  const [cssAnimationKey, setCssAnimationKey] = useState('');

  const { identifier } = useParams();

  const onItemClick = (e: any) => {
    setSliderContent(<LandscapeItem element={e.target.parentElement} />);
    setCssAnimationKey(e.target.parentElement.id);
    setShowSlider(true);
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
          <LandscapeSlider sliderContent={sliderContent} closeSlider={closeSlider} />
        </CSSTransition>
        <LandscapeMap identifier={identifier} onItemClick={onItemClick} closeSlider={closeSlider} />
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
