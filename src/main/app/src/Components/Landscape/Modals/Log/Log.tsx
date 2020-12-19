import React, { useState, useEffect, useCallback } from 'react';
import { ILandscape } from '../../../../interfaces';
import { get } from '../../../../utils/API/APIClient';

import LevelChip from '../../../LevelChip/LevelChip';
import { Card, Theme, Typography } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      margin: 5,
      padding: 5,
      backgroundColor: theme.palette.secondary.main,
      overflow: 'visible'
    },
  })
);

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
  const classes = useStyles();
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
    <Card className={classes.card}>
      <Typography variant={'h5'}>Process Log of '{landscape.name}' </Typography>
      <CardContent>{content}</CardContent>
    </Card>
  );
};

export default Log;