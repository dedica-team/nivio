import React, { ReactElement } from 'react';

import './LandscapeSlider.scss';

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
  return (
    <div className='slider'>
      <button className={'close'} onClick={closeSlider}>
        close
      </button>
      {sliderContent}
    </div>
  );
};

export default Landscape;
