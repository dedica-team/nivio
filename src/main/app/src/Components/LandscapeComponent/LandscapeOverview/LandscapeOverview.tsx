import React, { useState, useEffect, useCallback, ReactElement } from 'react';

import { ILandscape } from '../../../interfaces';
import LandscapeLog from '../Log/LandscapeLog';
import LandscapeOverviewLayout from './LandscapeOverviewLayout';
import { get } from '../../../utils/API/APIClient';

/**
 * Logic Component to display all available landscapes
 */

interface LandscapeLinks {
  _links: object;
}

const LandscapeOverview: React.FC = () => {
  const [landscapes, setLandscapes] = useState<ILandscape[] | null>();
  const [landscapeLinks, setLandscapeLinks] = useState<LandscapeLinks | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);
  const [sliderContent, setSliderContent] = useState<string | ReactElement | ReactElement[] | null>(
    null
  );
  const [showSlider, setShowSlider] = useState(false);
  const [cssAnimationKey, setCssAnimationKey] = useState('');

  //Could be moved into useEffect but can be used for a reload button later on
  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      setLandscapeLinks(await get('/api/'));
      let landscapeArray: ILandscape[] = [];
      if (landscapeLinks) {
        for (var landscapeLink in landscapeLinks._links) {
          const landscapeDescription = await get(`/api/${landscapeLink}`);
          if (landscapeDescription) {
            landscapeArray.push(landscapeDescription);
          }
        }
      }
      setLandscapes(landscapeArray);
      setLoadLandscapes(false);
    }
  }, [loadLandscapes, landscapeLinks]);

  const closeSlider = () => {
    setShowSlider(false);
  };

  const enterLog = (e: any, landscape: ILandscape) => {
    setSliderContent(<LandscapeLog landscape={landscape} closeSlider={closeSlider} />);
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
