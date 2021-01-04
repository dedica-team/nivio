import React, { useState, useEffect, useCallback } from 'react';
import { ILandscape } from '../../../../interfaces';
import { get } from '../../../../utils/API/APIClient';

import LevelChip from '../../../LevelChip/LevelChip';
import {Box, Typography} from '@material-ui/core';

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
const Log: React.FC<Props> = ({ landscape }) => {
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

  const content = data?.map((m, i) => {
    return (
      <div key={i}>
        <LevelChip level={m.level} title={m.date}/><br />
        <span className='logMessage'>{m.message}</span>
      </div>
    );
  });

  return (
    <Box m={2}>
      <Typography variant={'h5'}>Process Log of '{landscape.name}' </Typography>
      {content}
    </Box>
  );
};

export default Log;