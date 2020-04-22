import React, { useState, useEffect, Fragment } from 'react';
import { Link } from 'react-router-dom';
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
        <div>
          {ReactHtmlParser(html, {
            replace: (domNode) => {
              if (
                domNode.name === 'a' &&
                domNode.attribs &&
                domNode.children &&
                domNode.children[0]
              ) {
                let href = domNode.attribs['href'];
                const linkText = domNode.children[0].data;
                if (href.indexOf('http') !== -1) {
                  return;
                }

                // Remove anchors
                if (href.indexOf('#') !== -1) {
                  if (
                    (href.includes('#custom') || href.includes('#graph')) &&
                    usage !== 'extra.html' // Have to handle extra.html abit different for our sidebar
                  ) {
                    href = 'extra.html';
                  } else {
                    return <span>{linkText}</span>; // Convert to span if page is opened
                  }
                }

                return (
                  <Link
                    to={`/man/${href}`}
                    onClick={(e) => {
                      setTopic(href);
                    }}
                  >
                    {linkText}
                  </Link>
                );
              }

              if (domNode.attribs && domNode.attribs.class === 'logo') {
                return <Fragment />; // Remove Nivio Text because its already in our header
              }

              if (
                domNode.name &&
                domNode.name.includes('h') &&
                domNode.children &&
                domNode.children[1]
              ) {
                if (domNode.children[1].children && domNode.children[1].children[0].data === '¶') {
                  domNode.children[1] = <Fragment />; // Remove Anchors in titles, wont work with HashRouter
                }
              }
            },
          })}
        </div>
      </div>
      <Command />
    </div>
  );
};

export default Man;
