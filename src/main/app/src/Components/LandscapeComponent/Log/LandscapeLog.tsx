import React, { useState, useEffect } from 'react';
import { ILandscape } from '../../../interfaces';

import './LandscapeLog.scss';

interface Props {
  landscape: ILandscape;
}

interface Data {
  messages: string[];
}

const LandscapeLog: React.FC<Props> = ({ landscape }) => {
  const [data, setData] = useState<Data | null>(null);

  useEffect(() => {
    fetch(process.env.REACT_APP_BACKEND_URL + '/api/landscape/' + landscape.identifier + '/log')
      .then(response => {
        return response.json();
      })
      .then(json => {
        setData(json);
      });
  }, [landscape]);

  let content;
  if (!data) {
    content = 'loading...';
  } else {
    content = data.messages.map(m => {
      return (
        <div className={'item'} key={m}>
          {m}
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
