import React, { useState, useEffect } from 'react';
import { Divider, Chip } from '@material-ui/core';

interface Data {
  messages: Entry[];
}

interface Entry {
  event: string;
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
          <Chip label={m.landscape} /> {m.event}
          <Divider />
        </div>
      );
    });
  }

  return (
    <div className='events'>
      <span className='title'>Processing Event Log</span>
      <div className='itemContainer'>{content}</div>
    </div>
  );
};

export default Events;
