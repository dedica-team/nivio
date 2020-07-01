import React, { useState, useEffect, Fragment } from 'react';
import { Link } from 'react-router-dom';
import { useParams } from 'react-router-dom';
import ReactHtmlParser from 'html-react-parser';
import raw from 'raw.macro';

import './Man.scss';
import './pygments.scss';

const topics: any = {
  'install.html': raw('../../../../../../docs/build/install.html'),
  'input.html': raw('../../../../../../docs/build/input.html'),
  'output.html': raw('../../../../../../docs/build/output.html'),
  'magic.html': raw('../../../../../../docs/build/magic.html'),
  'model.html': raw('../../../../../../docs/build/model.html'),
  'references.html': raw('../../../../../../docs/build/references.html'),
  'index.html': raw('../../../../../../docs/build/index.html'),
  'assessment.html': raw('../../../../../../docs/build/assessment.html'),
};

/**
 * Renders nivio manual, depending on which url param is given
 */
const Man: React.FC = () => {
  const [html, setHtml] = useState<string>('<p>OOPS SOMETHING WENT WRONG :(</p>');
  let { usage } = useParams();
  if (usage == null || typeof usage == 'undefined') usage = 'index';
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
              // Handle Links
              if (
                domNode.name === 'a' &&
                domNode.attribs &&
                domNode.children &&
                domNode.children[0]
              ) {
                let href = domNode.attribs['href'];
                const linkText = domNode.children[0].data;
                if (href.indexOf('http') !== -1 && href.indexOf('http-api') === -1) {
                  return;
                }

                // Remove anchors
                if (href.indexOf('#') !== -1) {
                  if (
                    (href.includes('#custom-er-branding') ||
                      href.includes('#graph') ||
                      href.includes('#http-api')) &&
                    usage !== 'output.html' // Have to handle output.html abit different for our sidebar
                  ) {
                    href = 'output.html';
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

              // Remove Nivio Text because its already in our header
              if (domNode.attribs && domNode.attribs.class === 'logo') {
                return <Fragment />;
              }

              // Remove Anchors in titles, wont work with HashRouter
              if (
                domNode.name &&
                domNode.name.includes('h') &&
                domNode.children &&
                domNode.children[1]
              ) {
                if (
                  domNode.children[1].children &&
                  domNode.children[1].children[0].data &&
                  domNode.children[1].children[0].data === '¶'
                ) {
                  domNode.children[1] = <Fragment />;
                }
              }
            },
          })}
        </div>
      </div>
    </div>
  );
};

export default Man;
