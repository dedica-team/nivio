import React, { useState, useEffect } from 'react';
import { ILandscape } from '../../../interfaces';

interface Props {
  landscape: ILandscape;
  closeFn: () => void;
}

interface Data {
  messages: string[];
}

const LandscapeLog: React.FC<Props> = ({ landscape, closeFn }) => {
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
      return <div key={m}>{m}</div>;
    });
  }

  return (
    <div>
      <button className={'control'} onClick={closeFn} style={{ float: 'right' }}>
        close
      </button>
      <h1>Landscape {landscape.name} Process Log</h1>
      {content}
      <br />
    </div>
  );
};

export default LandscapeLog;
