import React, { useEffect, useState } from 'react';
import './LandscapeItem.scss';

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
  const [html, setHtml] = useState<string>(`Not Found :(`);
  const [topic, setTopic] = useState<string | null>(null);

  useEffect(() => {
    let topic = element.getAttribute('data-identifier');
    setTopic(topic);
    if (topic !== null) {
      console.log(topic);
      fetch(process.env.REACT_APP_BACKEND_URL + '/api/' + topic)
        .then((response) => {
          return response.json();
        })
        .then((data) => {
          setHtml(JSON.stringify(data));
        });
    }
  }, [element, topic, html]);

  return <div className='landscapeItemContent'>{html}</div>;
};

export default LandscapeItem;
