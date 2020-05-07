import React, { useState, useEffect, useCallback, ReactElement } from 'react';

import { ILandscape } from '../../../interfaces';
import LandscapeLog from '../Log/LandscapeLog';
import LandscapeOverviewLayout from './LandscapeOverviewLayout';

/**
 * Logic Component to display all available landscapes
 */

const LandscapeOverview: React.FC = () => {
  const [modalContent, setModalContent] = useState<string | ReactElement | ReactElement[] | null>(
    null
  );
  const [landscapes, setLandscapes] = useState<ILandscape[]>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);

  //Could be moved into useEffect but can be used for a reload button later on
  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      await fetch(process.env.REACT_APP_BACKEND_URL + '/api/')
        .then((response) => {
          return response.json();
        })
        .then((json) => {
          console.log(json);
          setLandscapes(json);
          setLoadLandscapes(false);
        });
    }
  }, [loadLandscapes]);

  useEffect(() => {
    getLandscapes();
  }, [getLandscapes]);

  const enterLog = (landscape: ILandscape) => {
    setModalContent(<LandscapeLog landscape={landscape} />);
  };

  return (
    <LandscapeOverviewLayout
      modalContent={modalContent}
      landscapes={landscapes}
      enterLog={enterLog}
    />
  );
};

export default LandscapeOverview;
