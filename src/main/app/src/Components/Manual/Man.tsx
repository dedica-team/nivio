import React, { Fragment, useCallback, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import ReactHtmlParser, { domToReact } from 'html-react-parser';

import { get } from '../../utils/API/APIClient';
import { DomElement } from 'htmlparser2';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { Box, Theme, Typography } from '@material-ui/core';
import { renderToString } from 'react-dom/server';

import './Man.css';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    manualContainer: {
      overflowY: 'scroll',
      height: '90vh',
      marginRight: 340,
    },
    link: {
      color: theme.palette.primary.contrastText,
    },
    emptyManualContainer: {
      flexGrow: 1,
    },
  })
);

interface Props {
  setSidebarContent: Function;
  setPageTitle: Function;
}

/**
 * Renders nivio manual, depending on which url param is given
 */
const Man: React.FC<Props> = ({ setSidebarContent, setPageTitle }) => {
  const classes = useStyles();
  const [html, setHtml] = useState<JSX.Element | JSX.Element[]>(
    <React.Fragment>
      <Typography align='center'>This manual page doesn't exist. :(</Typography>
    </React.Fragment>
  );
  let { usage } = useParams<{ usage: string }>();
  if (usage == null || typeof usage == 'undefined') usage = 'index';
  const [topic, setTopic] = useState<string>(usage + '');
  const [side, setSide] = useState<any>(null);
  const [emptyManual, setemptyManual] = useState<boolean>(false);

  const handleSphinxSidebar = useCallback(
    (domNode: DomElement) => {
      const replaceSphinx = (domNode: DomElement) => {
        // Handle Links
        if (domNode.name === 'a' && domNode.attribs && domNode.children && domNode.children[0]) {
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
              className={classes.link}
              to={`/man/${href}`}
              onClick={(e) => {
                setTopic(href);
              }}
            >
              {linkText}
            </Link>
          );
        }
      };
      // @ts-ignore
      const domToReact1: JSX.Element = domToReact([domNode]);
      const html = renderToString(domToReact1);
      const reactHtmlParser = ReactHtmlParser(html, { replace: replaceSphinx });
      setSide(reactHtmlParser);
    },
    [usage, classes]
  );

  useEffect(() => {
    setSidebarContent(null);
    setPageTitle('Manual');
  }, [setSidebarContent, setPageTitle]);

  useEffect(() => {
    if (!usage?.includes('.html')) {
      setTopic(usage + '.html');
    }
  }, [usage]);

  useEffect(() => {
    setSidebarContent(side);
  }, [side, setSidebarContent]);

  useEffect(() => {
    get(`/docs/${topic}`)
      .then((response) => {
        const replaceFunc = (domNode: DomElement) => {
          if (domNode.attribs && domNode.attribs.class === 'sphinxsidebar') {
            handleSphinxSidebar(domNode);
            return <React.Fragment />;
          }

          // Handle Links
          if (domNode.name === 'a' && domNode.attribs && domNode.children && domNode.children[0]) {
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
        };
        const parser = new DOMParser();
        const parsedHtml = parser.parseFromString(response, 'text/html');
        const body = parsedHtml.querySelector('body');

        if (body) {
          const reactHtmlParser: JSX.Element | JSX.Element[] = ReactHtmlParser(body.innerHTML, {
            replace: replaceFunc,
          });
          setHtml(reactHtmlParser);
          setemptyManual(true);
        }
      })
      .catch((reason) => console.error(reason));
  }, [topic, usage, handleSphinxSidebar]);

  return (
    <Box className={emptyManual ? classes.manualContainer : classes.emptyManualContainer}>
      {html}
    </Box>
  );
};

export default Man;
