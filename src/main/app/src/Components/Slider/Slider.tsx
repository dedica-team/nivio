import React, { ReactElement } from 'react';

import './Slider.scss';

interface Props {
  sliderContent: string | ReactElement | null;
  closeSlider(): void;
}

/**
 * Slider to display our LandscapeItem in a cool way
 * @param sliderContent Display Content
 * @param closeSlider Enable/Disable CSS Transition in Parent Component
 */
const Landscape: React.FC<Props> = ({ sliderContent, closeSlider }) => {
  document.onkeydown = (e) => {
    if (e.key === 'Escape') {
      closeSlider();
    }
  };
  return (
    <div className='slider'>
      <button className={'close'} onClick={closeSlider}>
        X
      </button>
      {sliderContent}
    </div>
  );
};

export default Landscape;
