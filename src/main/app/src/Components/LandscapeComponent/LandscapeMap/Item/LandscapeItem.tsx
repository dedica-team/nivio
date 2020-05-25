import React, { useEffect, useState, useCallback } from 'react';
import { get } from '../../../../utils/API/APIClient';
import './LandscapeItem.scss';

import { IItem } from '../../../../interfaces';

interface Props {
  element: Element;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 */
const LandscapeItem: React.FC<Props> = ({ element }) => {
  const [item, setItem] = useState<IItem | null>();
  const [loadItem, setLoadItem] = useState<boolean>(true);
  const [topic, setTopic] = useState<string | null>(null);

  const getItem = useCallback(async () => {
    if (loadItem && topic) {
      setItem(await get(`/api/${topic}`));
      setLoadItem(false);
    }
  }, [loadItem, topic]);

  useEffect(() => {
    let topic = element.getAttribute('data-identifier');
    setTopic(topic);
    if (topic !== null) {
      getItem();
    }
  }, [element, topic, getItem]);

  return (
    <div className='landscapeItemContent'>
      <p> Identifier: {item?.identifier}</p>
      <p> {item?.name ? `name: ${item?.name}` : ''}</p>
      <p> {item?.description ? `description: ${item?.description}` : ''}</p>
      <p> {item?.contact ? `contact: ${item?.contact} <br />` : ''}</p>
      <p> {item?.group ? `group: ${item?.group}` : ''}</p>
    </div>
  ); // TODO: styling
};

export default LandscapeItem;
