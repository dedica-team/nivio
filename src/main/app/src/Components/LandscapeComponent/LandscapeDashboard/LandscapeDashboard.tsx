import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';

import LandscapeDashboardLayout from './LandscapeDashboardLayout';
import { ILandscape } from '../../../interfaces';
import { get } from '../../../utils/API/APIClient';

/**
 * Logic Component to display all available landscapes
 */

const LandscapeDashboard: React.FC = () => {
  const [landscape, setLandscape] = useState<ILandscape | null>();
  const [loadLandscape, setLoadLandscape] = useState<boolean>(true);

  const { landscapeIdentifier } = useParams();

  const getLandscape = useCallback(async () => {
    if (loadLandscape) {
      setLandscape(await get(`/api/${landscapeIdentifier}`));
      if (landscape) {
        setLoadLandscape(false);
      }
    }
  }, [loadLandscape, landscape, landscapeIdentifier]);

  useEffect(() => {
    getLandscape();
  }, [getLandscape]);

  return <LandscapeDashboardLayout landscape={landscape} />;
};

export default LandscapeDashboard;
