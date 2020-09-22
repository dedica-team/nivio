import React, { useState, useEffect, useCallback, ReactElement, MouseEvent } from 'react';

import { ILandscape, ILandscapeLinks } from '../../../interfaces';
import Log from '../Modals/Log/Log';
import OverviewLayout from './OverviewLayout';
import Slider from '../../Slider/Slider';
import { get } from '../../../utils/API/APIClient';

/**
 * Logic Component to display all available landscapes
 */

const Overview: React.FC = () => {
  const [landscapes, setLandscapes] = useState<ILandscape[]>([]);
  const [landscapeLinks, setLandscapeLinks] = useState<ILandscapeLinks | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);
  const [sliderContent, setSliderContent] = useState<string | ReactElement | ReactElement[] | null>(
    null
  );
  const [showSlider, setShowSlider] = useState(false);

  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      setLandscapeLinks(await get('/api/'));
      if (landscapeLinks) {
        for (const landscapeLink in landscapeLinks._links) {
          const landscapeDescription: ILandscape | null = await get(
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
    const sliderContent = <Log landscape={landscape} />;
    setSliderContent(<Slider sliderContent={sliderContent} closeSlider={closeSlider} />);
    setShowSlider(true);
  };

  useEffect(() => {
    getLandscapes();
  }, [getLandscapes]);

  return (
    <OverviewLayout
      sliderContent={sliderContent}
      landscapes={landscapes}
      enterLog={enterLog}
      showSlider={showSlider}
    />
  );
};

export default Overview;
