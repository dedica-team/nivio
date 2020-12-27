import React, { MouseEvent, ReactElement, useCallback, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import {
  fitSelection,
  fitToViewer,
  ReactSVGPanZoom,
  setPointOnViewerCenter,
  Tool,
  TOOL_AUTO,
  Value,
} from 'react-svg-pan-zoom';

import './Map.scss';

import MapRelation from './MapRelation/MapRelation';
import { withBasePath } from '../../../utils/API/BasePath';
import { get } from '../../../utils/API/APIClient';
import { ReactSvgPanZoomLoaderXML } from './ReactSVGPanZoomLoaderXML';
import Item from '../Modals/Item/Item';
import { Theme, Toolbar } from '@material-ui/core';
import Dashboard from '../Dashboard/Dashboard';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { IAssessment, ILandscape } from '../../../interfaces';
import { getItem } from '../Utils/utils';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    bottomBar: {
      top: 'auto',
      bottom: 0,
      left: 5,
      padding: 5,
      position: 'fixed',
      width: '100%',
    },
  })
);

interface Props {
  setSidebarContent: Function;
  setFindFunction: Function;
  setPageTitle: Function;
}

interface SVGData {
  width: number;
  height: number;
  xml: string;
}

/**
 * Displays a chosen landscape as interactive SVG
 *
 * @param setSidebarContent function to set sidebar/drawer content
 * @param setFindFunction function to use to find an item. make sure to pass an anon func returning the actually used function
 * @param setPageTitle can be used to set the page title in parent state
 */
const Map: React.FC<Props> = ({ setSidebarContent, setFindFunction, setPageTitle }) => {
  const [tool, setTool] = useState<Tool>(TOOL_AUTO);

  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});
  const classes = useStyles();
  const [data, setData] = useState<SVGData | null>(null);
  const [renderWithTransition, setRenderWithTransition] = useState(false);
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);
  const { identifier } = useParams<{ identifier: string }>();
  const [landscape, setLandscape] = useState<ILandscape | null>();
  const [assessments, setAssessments] = useState<IAssessment | undefined>(undefined);

  const [isFirstRender, setIsFirstRender] = useState(true);

  const findItem = useCallback(
    (fullyQualifiedItemIdentifier: string) => {
      const element = document.getElementById(fullyQualifiedItemIdentifier);
      if (element) {
        let dataX = element.getAttribute('data-x');
        let dataY = element.getAttribute('data-y');
        if (dataX && dataY) {
          const shift: number = 0;
          const x = parseFloat(dataX) + shift;
          const y = parseFloat(dataY) + shift / 2;
          setValue(setPointOnViewerCenter(value, x, y, 1));
          setRenderWithTransition(true);
          setHighlightElement(element);
        }
      }
    },
    [value]
  );

  const onItemClick = (e: MouseEvent<HTMLElement>) => {
    const fullyQualifiedItemIdentifier = e.currentTarget.getAttribute('data-identifier');
    if (fullyQualifiedItemIdentifier && landscape) {
      let item = getItem(landscape, fullyQualifiedItemIdentifier);
      if (item)
        setSidebarContent(
          <Item key={fullyQualifiedItemIdentifier} useItem={item} findItem={findItem} />
        );
    }
  };

  const onRelationClick = (e: MouseEvent<HTMLElement>) => {
    const dataSource = e.currentTarget.getAttribute('data-source');
    let sourceElement, sourceX, sourceY;
    if (dataSource) {
      sourceElement = document.getElementById(dataSource);
      if (sourceElement) {
        sourceX = sourceElement.getAttribute('data-x');
        sourceY = sourceElement.getAttribute('data-y');
      }
    }

    const dataTarget = e.currentTarget.getAttribute('data-target');
    let targetElement, targetX, targetY;
    if (dataTarget) {
      targetElement = document.getElementById(dataTarget);
      if (targetElement) {
        targetX = targetElement.getAttribute('data-x');
        targetY = targetElement.getAttribute('data-y');
      }
    }

    if (sourceX && sourceY && targetX && targetY) {
      sourceX = parseFloat(sourceX) / 2;
      targetX = parseFloat(targetX) / 2;
      sourceY = parseFloat(sourceY) / 2;
      targetY = parseFloat(targetY) / 2;

      const x = (sourceX + targetX) / 2;
      const y = (sourceY + targetY) / 2;

      const zoomWidth = Math.abs(Math.min(sourceX, targetX)) + window.innerWidth;
      const zoomHeight = Math.abs(Math.min(sourceY, targetY)) + window.innerHeight * 0.92;

      setHighlightElement(e.currentTarget.children);
      setRenderWithTransition(true);
      setValue(fitSelection(value, x - 500, y, zoomWidth, zoomHeight));
    }

    const dataType = e.currentTarget.getAttribute('data-type');

    if (dataSource && dataTarget) {
      setSidebarContent(
        <MapRelation
          sourceIdentifier={dataSource}
          targetIdentifier={dataTarget}
          type={dataType}
          findItem={findItem}
        />
      );
    }
  };

  useEffect(() => {
    const route = withBasePath(`/render/${identifier}/map.svg`);
    get(route).then((svg) => {
      const parser = new DOMParser();
      const doc: any = parser.parseFromString(svg, 'image/svg+xml');
      const width = doc.firstElementChild.width.baseVal.value;
      const height = doc.firstElementChild.height.baseVal.value;
      setData({ width: width, height: height, xml: svg });
    });
  }, [identifier]);

  //load landscape
  useEffect(() => {
    if (!landscape) {
      get(`/api/${identifier}`).then((response) => {
        setLandscape(response);
        setPageTitle(response.name);
        if (findItem) {
          setFindFunction(() => findItem);
        }
      });

      get(`/assessment/${identifier}`).then((response) => {
        setAssessments(response);
      });
    }
  }, [identifier, setPageTitle, setFindFunction, findItem, landscape]);

  useEffect(() => {
    let timeout: NodeJS.Timeout;

    if (highlightElement instanceof Element) {
      highlightElement.classList.add('highlightLabel');

      timeout = setTimeout(() => {
        highlightElement.classList.remove('highlightLabel');
        setRenderWithTransition(false);
        setHighlightElement(null);
      }, 2000);
    }

    if (highlightElement instanceof HTMLCollection) {
      for (const element in highlightElement) {
        if (!isNaN(+element)) {
          if (highlightElement[element].tagName === 'path') {
            highlightElement[element].classList.add('highlightRelation');
            break;
          }
          highlightElement[element].classList.add('highlightLabel');
        }
      }

      timeout = setTimeout(() => {
        for (const element in highlightElement) {
          if (!isNaN(+element)) {
            highlightElement[element].classList.remove('highlightRelation');
            highlightElement[element].classList.remove('highlightLabel');
          }
        }
        setRenderWithTransition(false);
        setHighlightElement(null);
      }, 2000);
    }

    return () => clearTimeout(timeout);
  }, [highlightElement]);

  if (data) {
    if (isFirstRender && value.a != null) {
      // @ts-ignore
      setValue(fitToViewer(value, 'center', 'center'));
      setIsFirstRender(false);
    }

    return (
      <div className='landscapeMapContainer'>
        <ReactSvgPanZoomLoaderXML
          xml={data.xml}
          proxy={
            <>
              <SvgLoaderSelectElement selector='.item' onClick={onItemClick} />
              <SvgLoaderSelectElement selector='.relation' onClick={onRelationClick} />
            </>
          }
          render={(content: ReactElement[]) => (
            <ReactSVGPanZoom
              key={'panzoom'}
              width={window.innerWidth}
              height={window.innerHeight * 0.92}
              background={'transparent'}
              miniatureProps={{
                position: 'none',
                background: '#616264',
                width: 200,
                height: 300,
              }}
              preventPanOutside={false}
              toolbarProps={{ position: 'none' }}
              detectAutoPan={false}
              tool={tool}
              onChangeTool={(newTool) => setTool(newTool)}
              value={value}
              onChangeValue={(newValue) => setValue(newValue)}
              className={`ReactSVGPanZoom ${renderWithTransition ? 'with-transition' : ''}`}
            >
              <svg width={data?.width} height={data?.height}>
                {content}
              </svg>
            </ReactSVGPanZoom>
          )}
        />
        <Toolbar className={classes.bottomBar}>
          {landscape && assessments && (
            <Dashboard
              findItem={findItem}
              setSidebarContent={setSidebarContent}
              landscape={landscape}
              assessments={assessments}
            />
          )}
        </Toolbar>
      </div>
    );
  }
  return <span>loading...</span>;
};

export default Map;
