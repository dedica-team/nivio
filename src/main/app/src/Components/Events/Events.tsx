import React, { useState, useEffect, useCallback } from 'react';
import { get } from '../../utils/API/APIClient';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../Resources/styling/ComponentStyles';
import LevelChip from '../LevelChip/LevelChip';

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
  const componentClasses = componentStyles();

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
        <LevelChip level={m.level} title={m.date}>
          {m.level}
        </LevelChip>
        <br />
        <strong>{m.landscape}</strong>: {m.message}
        <br />
        <br />
      </div>
    );
  });

  return (
    <Card className={componentClasses.card}>
      <CardHeader title={'Processing Event Log'} />
      <CardContent>{content}</CardContent>
    </Card>
  );
};

export default Events;
