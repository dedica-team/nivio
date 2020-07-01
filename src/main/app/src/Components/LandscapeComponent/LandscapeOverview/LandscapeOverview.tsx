import React, { useState, useEffect, useCallback, ReactElement } from 'react';

import { ILandscape } from '../../../interfaces';
import LandscapeLog from '../Log/LandscapeLog';
import LandscapeOverviewLayout from './LandscapeOverviewLayout';
import { get } from '../../../utils/API/APIClient';

/**
 * Logic Component to display all available landscapes
 */

const LandscapeOverview: React.FC = () => {
  const [landscapes, setLandscapes] = useState<ILandscape[] | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);
  const [sliderContent, setSliderContent] = useState<string | ReactElement | ReactElement[] | null>(
    null
  );
  const [showSlider, setShowSlider] = useState(false);
  const [cssAnimationKey, setCssAnimationKey] = useState('');

  //Could be moved into useEffect but can be used for a reload button later on
  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      setLandscapes(await get('/api/'));
      setLoadLandscapes(false);
    }
  }, [loadLandscapes]);

  const enterLog = (e: any, landscape: ILandscape) => {
    setSliderContent(<LandscapeLog landscape={landscape} />);
    setCssAnimationKey(e.target.parentElement.id);
    setShowSlider(true);
  };

  useEffect(() => {
    getLandscapes();
  }, [getLandscapes]);

  return (
    <LandscapeOverviewLayout
      sliderContent={sliderContent}
      landscapes={landscapes}
      enterLog={enterLog}
      showSlider={showSlider}
      cssAnimationKey={cssAnimationKey}
    />
  );
};

export default LandscapeOverview;
