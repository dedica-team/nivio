import React, { useState, useEffect } from 'react';
import { Divider } from '@material-ui/core';
import './Events.scss';
import TitleBar from "../TitleBarComponent/TitleBar";
import LevelChip from "../LevelChipComponent/LevelChip";
interface Data {
  messages: Entry[];
}

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
  const [data, setData] = useState<Data | null>(null);

  useEffect(() => {
    fetch(process.env.REACT_APP_BACKEND_URL + '/events')
      .then((response) => {
        return response.json();
      })
      .then((json) => {
        setData({ messages: json });
      });
  }, []);

  let content;
  if (!data) {
    content = 'loading...';
  } else {
    content = data.messages.map((m, i) => {
      return (
        <div className={'item'} key={i}>
          <span className={'date'}>{m.date}</span> <LevelChip level={m.level} title={m.landscape}/> <span className={'tyoe'}>{m.type}</span> {m.message}
          <Divider />
        </div>
      );
    });
  }

  return (
    <div className='events'>
      <TitleBar title={'Processing Event Log'} />
      <div className='itemContainer'>{content}</div>
    </div>
  );
};

export default Events;
