import React, {
  MouseEvent,
  ReactElement,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react';
import { useParams } from 'react-router-dom';

import { SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';

import './Map.css';

import MapRelation from './MapRelation/MapRelation';
import { withBasePath } from '../../../utils/API/BasePath';
import { get } from '../../../utils/API/APIClient';
import { ReactSvgPanZoomLoaderXML } from './ReactSVGPanZoomLoaderXML';
import Item from '../Modals/Item/Item';
import { getGroup, getItem } from '../Utils/utils';
import Group from '../Modals/Group/Group';
import { LocateFunctionContext } from '../../../Context/LocateFunctionContext';
import ZoomOutIcon from '@material-ui/icons/ZoomOut';
import IconButton from '@material-ui/core/IconButton';
import { createStyles, darken, Theme } from '@material-ui/core';
import makeStyles from '@material-ui/core/styles/makeStyles';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import { getApproximateCenterCoordinates, getCorrected } from './MapUtils';
import {
  fitToViewer,
  ReactSVGPanZoom,
  setPointOnViewerCenter,
  TOOL_AUTO,
  Value,
} from 'react-svg-pan-zoom';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    menuIcon: {
      position: 'absolute',
      cursor: 'pointer',
      zIndex: 1000,
      left: 20,
      backgroundColor: darken(theme.palette.primary.main, 0.2),
    },
  })
);

interface Props {
  setSidebarContent: Function;
  setPageTitle: Function;
}

interface SVGData {
  width: number;
  height: number;
  viewBox: SVGRect;
  xml: string;
}

/**
 * Displays a chosen landscape as interactive SVG
 *
 * @param setSidebarContent function to set sidebar/drawer content
 * @param setLocateFunction function to use to find an item. make sure to pass an anon func returning the actually used function
 * @param setPageTitle can be used to set the page title in parent state
 */
const Map: React.FC<Props> = ({ setSidebarContent, setPageTitle }) => {
  const classes = useStyles();
  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});
  const [data, setData] = useState<SVGData | null>(null);
  const [renderWithTransition, setRenderWithTransition] = useState(false);
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);
  const { identifier } = useParams<{ identifier: string }>();

  const [isFirstRender, setIsFirstRender] = useState(true);
  const [isZoomed, setIsZoomed] = useState<Boolean>(false);

  const locateFunctionContext = useContext(LocateFunctionContext);
  const landscapeContext = useContext(LandscapeContext);

  const locateComponent = useCallback(
    (fullyQualifiedItemIdentifier: string) => {
      const element = document.getElementById(fullyQualifiedItemIdentifier);
      if (element) {
        let dataX = Number(element.getAttribute('data-x'));
        let dataY = Number(element.getAttribute('data-y'));
        if (dataX && dataY && data) {
          const coords = getApproximateCenterCoordinates(
            data.viewBox,
            data.width,
            data.height,
            dataX,
            dataY
          );

          setValue(setPointOnViewerCenter(value, coords.x, coords.y, 0.5));
          setRenderWithTransition(true);
          setHighlightElement(element);
          setIsZoomed(true);
        }
      }
    },
    [data, value]
  );

  const onItemClick = (e: MouseEvent<HTMLElement>) => {
    const fullyQualifiedItemIdentifier = e.currentTarget.getAttribute('data-identifier');
    if (fullyQualifiedItemIdentifier && landscapeContext.landscape) {
      let item = getItem(landscapeContext.landscape, fullyQualifiedItemIdentifier);
      if (item)
        setSidebarContent(
          <Item
            fullyQualifiedItemIdentifier={item.fullyQualifiedIdentifier}
            key={`item_${item.fullyQualifiedIdentifier}_${Math.random()}`}
          />
        );
    }
  };

  const onGroupClick = (e: MouseEvent<HTMLElement>) => {
    const fqi = e.currentTarget.getAttribute('data-identifier');
    if (fqi && landscapeContext.landscape) {
      let group = getGroup(landscapeContext.landscape, fqi);
      if (group) setSidebarContent(<Group group={group} key={`group_${fqi}_${Math.random()}`} />);
    }
  };

  const onRelationClick = (e: MouseEvent<HTMLElement>) => {
    if (!landscapeContext.landscape) return;

    const dataSource = e.currentTarget.getAttribute('data-source');
    let source, sourceElement, sourceX, sourceY;
    if (dataSource) {
      sourceElement = document.getElementById(dataSource);
      source = getItem(landscapeContext.landscape, dataSource);
      if (sourceElement) {
        sourceX = Number(sourceElement.getAttribute('data-x'));
        sourceY = Number(sourceElement.getAttribute('data-y'));
      }
    }

    const dataTarget = e.currentTarget.getAttribute('data-target');
    let target, targetElement, targetX, targetY;
    if (dataTarget) {
      targetElement = document.getElementById(dataTarget);
      target = getItem(landscapeContext.landscape, dataTarget);
      if (targetElement) {
        targetX = Number(targetElement.getAttribute('data-x'));
        targetY = Number(targetElement.getAttribute('data-y'));
      }
    }

    setHighlightElement(e.currentTarget.children);
    setRenderWithTransition(true);

    if (data && sourceX && sourceY && targetX && targetY) {
      const minX = Math.min(sourceX, targetX);
      const minY = Math.min(sourceY, targetY);

      let centerX = minX + (Math.max(sourceX, targetX) - minX) / 2;
      const correctedX = getCorrected(data.viewBox.x, centerX, data.width);
      let centerY = minY + (Math.max(sourceY, targetY) - minY) / 2;
      const correctedY = getCorrected(data.viewBox.y, centerY, data.height);
      setValue(setPointOnViewerCenter(value, correctedX, correctedY, 0.3));
      setIsZoomed(true);
    }

    if (source && target && dataTarget) {
      const relId = source.fullyQualifiedIdentifier + ';' + dataTarget;
      let relation = source.relations[relId];
      setSidebarContent(
        <MapRelation
          relation={relation}
          source={source}
          target={target}
          key={`relation_${relId}_${Math.random()}`}
        />
      );
    }
  };

  const loadMap = useCallback(() => {
    console.debug('loading map');
    const route = withBasePath(`/render/${identifier}/map.svg`);
    get(route).then((svg) => {
      const parser = new DOMParser();
      const doc: any = parser.parseFromString(svg, 'image/svg+xml');
      const width = doc.firstElementChild.width.baseVal.value;
      const height = doc.firstElementChild.height.baseVal.value;
      const viewBox: SVGRect = doc.firstElementChild.viewBox.baseVal;
      setData({ width: width, height: height, viewBox: viewBox, xml: svg });
    });
  }, [identifier, setData]);

  useEffect(() => {
    loadMap();
    setSidebarContent(null);
  }, [identifier, loadMap, setSidebarContent]);

  //load landscape
  useEffect(() => {
    if (landscapeContext.landscape) {
      setPageTitle(landscapeContext.landscape.name);
    }
  }, [setPageTitle, landscapeContext]);

  useEffect(() => {
    if (locateComponent) {
      locateFunctionContext.setLocateFunction(() => locateComponent);
    }
  }, [locateComponent, locateFunctionContext]);

  /**
   * Reload map on notification messages.
   */
  useEffect(() => {
    if (
      landscapeContext.notification?.type === 'ProcessingFinishedEvent' &&
      landscapeContext.notification?.landscape === landscapeContext.identifier
    ) {
      loadMap();
    }
  }, [landscapeContext.notification, landscapeContext.identifier, loadMap]);

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
      setValue(fitToViewer(value));
      setIsFirstRender(false);
    }

    return (
      <div className='landscapeMapContainer'>
        {isZoomed && (
          <IconButton
            className={classes.menuIcon}
            title={'Click to reset view'}
            onClick={() => {
              // @ts-ignore
              setValue(fitToViewer(value, 'center', 'center'));
              setIsZoomed(false);
            }}
            size={'small'}
          >
            <ZoomOutIcon></ZoomOutIcon>
          </IconButton>
        )}
        <ReactSvgPanZoomLoaderXML
          xml={data.xml}
          proxy={
            <>
              <SvgLoaderSelectElement
                selector='.item'
                onMouseUp={onItemClick}
                onTouchEnd={onItemClick}
              />
              <SvgLoaderSelectElement
                selector='.relation'
                onMouseUp={onRelationClick}
                onTouchEnd={onRelationClick}
              />
              <SvgLoaderSelectElement
                selector='.groupArea'
                onMouseUp={onGroupClick}
                onTouchEnd={onGroupClick}
              />
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
              onZoom={() => {
                setIsZoomed(true);
              }}
              tool={TOOL_AUTO}
              onChangeValue={(newValue: Value) => setValue(newValue)}
              onChangeTool={() => {}}
              value={value}
              className={`ReactSVGPanZoom ${renderWithTransition ? 'with-transition' : ''}`}
            >
              <svg width={data?.width} height={data?.height}>
                {content}
              </svg>
            </ReactSVGPanZoom>
          )}
        />
      </div>
    );
  }
  return <span>loading...</span>;
};

export default Map;
