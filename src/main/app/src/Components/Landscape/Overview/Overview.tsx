import React, { useState, useEffect, useCallback } from 'react';

import { ILandscape, ILandscapeLinks } from '../../../interfaces';
import OverviewLayout from './OverviewLayout';
import { get } from '../../../utils/API/APIClient';
import Events from '../../Events/Events';
import { Box } from '@material-ui/core';
import { Redirect } from 'react-router-dom';
/**
 * Logic Component to display all available landscapes
 */

interface Props {
  setSidebarContent: Function;
  setPageTitle: Function;
}

const Overview: React.FC<Props> = ({ setSidebarContent, setPageTitle }) => {
  const [landscapes, setLandscapes] = useState<ILandscape[]>([]);
  const [landscapeLinks, setLandscapeLinks] = useState<ILandscapeLinks | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);
  const [landscapesCount, setLandscapesCount] = useState<Number>(0);


  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      setLandscapeLinks(await get('/api/'));
      if (landscapeLinks) {
        setLandscapesCount(Object.keys(landscapeLinks._links).length);
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
    setPageTitle('All Landscapes');
  }, [getLandscapes, setSidebarContent, setPageTitle]);


  return (
    landscapes.length > 0 ?
      landscapesCount > 1 ? <OverviewLayout landscapes={landscapes} setSidebarContent={setSidebarContent} /> :
        <Redirect to={`/landscape/${landscapes[0]?.identifier}`} />
      : <Box>Loading landscapes...</Box>)
};

export default Overview;
