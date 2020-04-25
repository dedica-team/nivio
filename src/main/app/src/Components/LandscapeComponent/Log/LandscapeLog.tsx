import React, { useState, useEffect } from 'react';
import { ILandscape } from '../../../interfaces';

import './LandscapeLog.scss';
import { Chip, Avatar } from '@material-ui/core';
import LevelChip from "../../LevelChipComponent/LevelChip";

interface Props {
  landscape: ILandscape;
}

interface Data {
  messages: Entry[];
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
  const [data, setData] = useState<Data | null>(null);

  useEffect(() => {
    fetch(process.env.REACT_APP_BACKEND_URL + '/api/landscape/' + landscape.identifier + '/log')
      .then((response) => {
        return response.json();
      })
      .then((json) => {
        setData(json);
      });
  }, [landscape]);

  let content;
  if (!data) {
    content = 'loading...';
  } else {
    content = data.messages.map((m, i) => {
      return (
        <div className={'item'} key={i}>
          <LevelChip level={m.level} title={m.date}></LevelChip>
          {m.message}
        </div>
      );
    });
  }

  return (
    <div className='logContent'>
      <span className='title'>Landscape {landscape.name} Process Log</span>
      <div className='itemContainer'>{content}</div>
    </div>
  );
};

export default LandscapeLog;
