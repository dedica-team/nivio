import React, { useState, useEffect, useCallback } from 'react';
import { ILandscape } from '../../../interfaces';
import { getLandscapeLog } from '../../../utils/APIClient';

import './LandscapeLog.scss';
import LevelChip from '../../LevelChipComponent/LevelChip';

interface Props {
  landscape: ILandscape;
}

interface Entry {
  level: string;
  message: string;
  date: string;
}

/**
 * Gets all logs from the backend for a landscape
 * @param landscape Landscape of which the log is to be shown
 */
const LandscapeLog: React.FC<Props> = ({ landscape }) => {
  const [data, setData] = useState<Entry[] | null>(null);
  const [loadData, setLoadData] = useState<boolean>(true);

  const getLog = useCallback(async () => {
    if (loadData) {
      setData(await getLandscapeLog(landscape.identifier));
      setLoadData(false);
    }
  }, [loadData, landscape]);

  useEffect(() => {
    getLog();
  }, [getLog]);

  const content = data?.map((m, i) => {
    return (
      <div className={'item'} key={i}>
        <LevelChip level={m.level} title={m.date}></LevelChip>
        {m.message}
      </div>
    );
  });

  return (
    <div className='logContent'>
      <span className='title'>Landscape {landscape.name} Process Log</span>
      <div className='itemContainer'>{content}</div>
    </div>
  );
};

export default LandscapeLog;
