import React, { useEffect, useState } from 'react';
import './LandscapeItem.scss';

import { IItem } from '../../../interfaces';

interface Props {
  element: Element;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 * TODO load assessment data
 * TODO maybe use data from landscape context
 */
const LandscapeItem: React.FC<Props> = ({ element }) => {
  const [item, setItem] = useState<IItem>();
  const [topic, setTopic] = useState<string | null>(null);

  useEffect(() => {
    let topic = element.getAttribute('data-identifier');
    setTopic(topic);
    if (topic !== null) {
      fetch(process.env.REACT_APP_BACKEND_URL + '/api/' + topic)
        .then((response) => {
          return response.json();
        })
        .then((data) => {
          setItem(data);
        });
    }
  }, [element, topic]);

  return <div className='landscapeItemContent'>{JSON.stringify(item)}</div>; // TODO: Extract variables and put it into form
};

export default LandscapeItem;
