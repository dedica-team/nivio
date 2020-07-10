import React, { useState, useEffect, useCallback, ReactElement, MouseEvent } from 'react';

import { ILandscape, ILandscapeLinks } from '../../../interfaces';
import LandscapeLog from '../Log/LandscapeLog';
import LandscapeOverviewLayout from './LandscapeOverviewLayout';
import Slider from '../../SliderComponent/Slider';
import { get, getJsonFromUrl } from '../../../utils/API/APIClient';

/**
 * Logic Component to display all available landscapes
 */

const LandscapeOverview: React.FC = () => {
  const [landscapes, setLandscapes] = useState<ILandscape[]>([]);
  const [landscapeLinks, setLandscapeLinks] = useState<ILandscapeLinks | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);
  const [sliderContent, setSliderContent] = useState<string | ReactElement | ReactElement[] | null>(
    null
  );
  const [showSlider, setShowSlider] = useState(false);
  const [cssAnimationKey, setCssAnimationKey] = useState('');

  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      setLandscapeLinks(await get('/api/'));
      if (landscapeLinks) {
        for (const landscapeLink in landscapeLinks._links) {
          const landscapeDescription: ILandscape | null = await getJsonFromUrl(
            landscapeLinks._links[landscapeLink].href
          );
          if (landscapeDescription) {
            setLandscapes((oldLandscapes) => [...oldLandscapes, landscapeDescription]);
          }
        }
      }
      setLoadLandscapes(false);
    }
  }, [loadLandscapes, landscapeLinks]);

  const closeSlider = () => {
    setShowSlider(false);
  };

  const enterLog = (e: MouseEvent<HTMLButtonElement>, landscape: ILandscape) => {
    const sliderContent = <LandscapeLog landscape={landscape} />;
    setSliderContent(<Slider sliderContent={sliderContent} closeSlider={closeSlider} />);
    setCssAnimationKey(e.currentTarget.id);
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
