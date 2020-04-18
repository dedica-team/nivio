import React, { useEffect, useState } from 'react';
import ReactHtmlParser from 'html-react-parser';

import './LandscapeItem.scss';

interface Props {
  element: Element;
}

/**
 * Returns a choosen Landscape Item if informations are available
 * @param element Choosen SVG Element from our Landscape Component
 */
const LandscapeItem: React.FC<Props> = ({ element }) => {
  const [html, setHtml] = useState<string>(`<h2>Not Found :(</h2>`);
  const [topic, setTopic] = useState<string | null>(null);

  useEffect(() => {
    let topic = element.getAttribute('data-identifier');
    setTopic(topic);
    if (topic !== null) {
      fetch(process.env.REACT_APP_BACKEND_URL + '/docs/item/' + topic)
        .then((response) => {
          return response.text();
        })
        .then((text) => {
          const parser = new DOMParser();
          const html = parser.parseFromString(text, 'text/html');
          let card = html.querySelector('.card-body');
          if (card) {
            setHtml(card.innerHTML);
          }
        });
    }
  }, [element, topic, html]);

  return <div className='landscapeItemContent'>{ReactHtmlParser(html)}</div>;
};

export default LandscapeItem;
