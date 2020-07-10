import React, { useEffect, useState, useCallback } from 'react';
import { get } from '../../../utils/API/APIClient';
import './LandscapeItem.scss';

import { IItem } from '../../../interfaces';

interface Props {
  fullyQualifiedItemIdentifier: string;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 */
const LandscapeItem: React.FC<Props> = ({ fullyQualifiedItemIdentifier }) => {
  const [item, setItem] = useState<IItem | null>();

  const getItem = useCallback(async () => {
    setItem(await get(`/api/${fullyQualifiedItemIdentifier}`));
  }, [fullyQualifiedItemIdentifier]);

  useEffect(() => {
    getItem();
  }, [fullyQualifiedItemIdentifier, getItem]);

  return (
    <div className='landscapeItemContent'>
      <p> Identifier: {item?.identifier}</p>
      <p> {item?.name ? `name: ${item?.name}` : ''}</p>
      <p> {item?.description ? `description: ${item?.description}` : ''}</p>
      <p> {item?.contact ? `contact: ${item?.contact} <br />` : ''}</p>
    </div>
  ); // TODO: styling
};

export default LandscapeItem;
