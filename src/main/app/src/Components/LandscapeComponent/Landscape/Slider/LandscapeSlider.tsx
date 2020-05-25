import React, { ReactElement } from 'react';

import './LandscapeSlider.scss';

interface Props {
  sliderContent: string | ReactElement | null;
  closeSlider(): void;
}

/**
 * Displays a choosen landscape as interactive SVG
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
