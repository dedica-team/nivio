import React, { useState, useEffect, useCallback } from 'react';

import { ILandscape, ILandscapeLinks } from '../../../interfaces';
import OverviewLayout from './OverviewLayout';
import { get } from '../../../utils/API/APIClient';
import Events from '../../Events/Events';

/**
 * Logic Component to display all available landscapes
 */

interface Props {
  setSidebarContent: Function;
}

const Overview: React.FC<Props> = ({ setSidebarContent }) => {
  const [landscapes, setLandscapes] = useState<ILandscape[]>([]);
  const [landscapeLinks, setLandscapeLinks] = useState<ILandscapeLinks | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);

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

  useEffect(() => {
    getLandscapes();
    setSidebarContent(<Events />);
  }, [getLandscapes]);

  return <OverviewLayout landscapes={landscapes} setSidebarContent={setSidebarContent} />;
};

export default Overview;