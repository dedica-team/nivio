import React, { useState, useEffect, useCallback } from 'react';
import { get } from '../../utils/API/APIClient';
import {Box, Typography} from "@material-ui/core";

interface Entry {
  type: string;
  message: string;
  level: string;
  landscape: string;
  date: string;
}

/**
 * Gets all events.
 *
 *
 */
const Events: React.FC = () => {
  const [data, setData] = useState<Entry[] | null>(null);
  const [loadData, setLoadData] = useState<boolean>(true);

  const loadEvents = useCallback(async () => {
    if (loadData) {
      setData(await get('/events'));
      setLoadData(false);
    }
  }, [loadData]);

  useEffect(() => {
    loadEvents();
  }, [loadEvents]);

  const content = data?.map((m, i) => {
    return (
      <div key={m.date + i}>
        <span className='date'>{m.date}</span><br />
        <strong>{m.level}</strong> <span className='message'>{m.landscape} {m.message}</span><br /><br />
      </div>
    );
  });

  return (
    <Box m={2} color={'secondary'}>
      <Typography variant={'h5'}>Processing Event Log</Typography>
      <div className='itemContainer'>{content}</div>
    </Box>
  );
};

export default Events;
