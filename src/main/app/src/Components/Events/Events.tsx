import React, { useState, useEffect, useCallback } from 'react';
import './Events.scss';
import TitleBar from '../TitleBar/TitleBar';
import LevelChip from '../LevelChip/LevelChip';
import { get } from '../../utils/API/APIClient';

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
const Events: React.FC<{}> = () => {
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
      <div className='item' key={i}>
        <span className='date'>{m.date}</span> <LevelChip level={m.level} title={m.landscape} />
        <span className='type'>{m.type}</span> <span className='message'> {m.message}</span>
      </div>
    );
  });

  return (
    <div className='events'>
      <TitleBar title={'Processing Event Log'} />
      <div className='itemContainer'>{content}</div>
    </div>
  );
};

export default Events;
