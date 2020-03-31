import React, { useEffect, useState } from 'react';
import ReactHtmlParser from 'react-html-parser';

interface Props {
  host: string;
  element: Element;
  closeFn: () => void;
}

const LandscapeItem: React.FC<Props> = ({ element, host, closeFn }) => {
  const [html, setHtml] = useState<string>(`<h2>Not Found :(</h2>`);
  const [topic, setTopic] = useState<string | null>(null);

  useEffect(() => {
    setTopic(element.getAttribute('data-identifier'));
    if (html === undefined && topic !== null) {
      console.log('in it');
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
          } else {
            setHtml(`<h2>Not Found :(</h2>`);
          }
        });
    }
  }, [element, host, topic, html]);

  return (
    <div>
      <button className={'control'} onClick={closeFn} style={{ float: 'right' }}>
        OK
      </button>
      <div>
        {' '}
        {ReactHtmlParser(html)}
        <br />
        <br />
      </div>
    </div>
  );
};

export default LandscapeItem;
