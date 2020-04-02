import React, { useEffect, useState } from 'react';
import ReactHtmlParser from 'html-react-parser';

interface Props {
  host: string;
  element: Element;
}

const LandscapeItem: React.FC<Props> = ({ element, host }) => {
  const [html, setHtml] = useState<string>(`<h2>Not Found :(</h2>`);
  const [topic, setTopic] = useState<string | null>(null);

  useEffect(() => {
    setTopic(element.getAttribute('data-identifier'));
    if (html === undefined && topic !== null) {
      fetch(host + '/docs/item/' + topic)
        .then(response => {
          return response.text();
        })
        .then(text => {
          const parser = new DOMParser();
          const html = parser.parseFromString(text, 'text/html');
          let card = html.querySelector('.card-body');
          if (card) {
            setHtml(card.innerHTML);
          }
        });
    }
  }, [element, host, topic, html]);

  return <div className='landscapeItemContent'>{ReactHtmlParser(html)}</div>;
};

export default LandscapeItem;
