import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import ReactHtmlParser from 'html-react-parser';
import raw from 'raw.macro';

import Command from '../CommandComponent/Command';

import './Man.scss';
import './pygments.scss';

const topics: any = {
  'install.html': raw('../../../../../../docs/build/install.html'),
  // features: raw('../../../../../../docs/build/features.html'),
  'input.html': raw('../../../../../../docs/build/input.html'),
  'extra.html': raw('../../../../../../docs/build/extra.html'),
  'api.html': raw('../../../../../../docs/build/api.html'),
  'magic.html': raw('../../../../../../docs/build/magic.html'),
  'model.html': raw('../../../../../../docs/build/model.html'),
  'references.html': raw('../../../../../../docs/build/references.html'),
  'index.html': raw('../../../../../../docs/build/index.html'),
};

/**
 * Renders nivio manual, depending on which url param is given
 */
const Man: React.FC = () => {
  const [html, setHtml] = useState<string>('<p>OOPS SOMETHING WENT WRONG :(</p>');
  const { usage } = useParams();
  const [topic, setTopic] = useState<string>(usage + '');

  useEffect(() => {
    if (!usage?.includes('.html')) {
      setTopic(usage + '.html');
    }
  }, [usage]);

  useEffect(() => {
    const rawHtml = topics[topic];
    const parser = new DOMParser();
    const parsedHtml = parser.parseFromString(rawHtml, 'text/html');
    const body = parsedHtml.querySelector('body');

    if (body) {
      setHtml(body.innerHTML);
    }
  }, [topic]);

  return (
    <div className='manualContainer'>
      <div className='manualContent'>
        <h1>Manual</h1>
        <div>{ReactHtmlParser(html)}</div>
      </div>
      <Command />
    </div>
  );
};

export default Man;
