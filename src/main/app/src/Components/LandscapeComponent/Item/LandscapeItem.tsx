import React, { useEffect, useState } from 'react';
import './LandscapeItem.scss';

import { IItem } from '../../../interfaces';

interface Props {
  element: Element;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
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

  return (
    <div className='landscapeItemContent'>
      <p> {item?.name ? `name: ${item?.name}` : ''}</p>
      <p> {item?.description ? `description: ${item?.description}` : ''}</p>
      <p> {item?.contact ? `contact: ${item?.contact} <br />` : ''}</p>
      <p> {item?.group ? `group: ${item?.group}` : ''}</p>
    </div>
  ); // TODO: styling
};

export default LandscapeItem;
