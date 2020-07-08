import React, { useState, useEffect, useCallback } from 'react';
import { ILandscape } from '../../../interfaces';
import { get } from '../../../utils/API/APIClient';

import './LandscapeLog.scss';
import LevelChip from '../../LevelChipComponent/LevelChip';
import Grid from '@material-ui/core/Grid';

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
      const log: any = await get(`/api/landscape/${landscape.identifier}/log`);
      if (log) {
        setData(log.messages);
        setLoadData(false);
      }
    }
  }, [loadData, landscape]);

  useEffect(() => {
    getLog();
  }, [getLog]);

  /*
    value         |0px     600px    960px    1280px   1920px
    key           |xs      sm       md       lg       xl
    screen width  |--------|--------|--------|--------|-------->
    range         |   xs   |   sm   |   md   |   lg   |   xl
  */
  const content = data?.map((m, i) => {
    return (
      <Grid key={i} className={'logContainer'} container spacing={0}>
        <Grid item xs={12} sm={6} md={4}>
          <LevelChip level={m.level} title={m.date}></LevelChip>
        </Grid>
        <Grid item xs={12} sm={6} md={8}>
          <span className='logMessage'>{m.message}</span>
        </Grid>
      </Grid>
    );
  });

  return (
    <div className='logContent'>
      <span className='title'>{landscape.name} Process Log</span>
      {content}
    </div>
  );
};

export default LandscapeLog;
