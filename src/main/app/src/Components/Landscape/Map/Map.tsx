import React, {
  MouseEvent,
  ReactElement,
  useCallback,
  useContext,
  useEffect,
  useState,
} from 'react';

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
import {
  fitToViewer,
  ReactSVGPanZoom,
  setPointOnViewerCenter,
  TOOL_AUTO,
  Value,
} from 'react-svg-pan-zoom';

const sidebarWidth = 280;
const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    menuIcon: {
      position: 'absolute',
      cursor: 'pointer',
      zIndex: 1000,
      left: 20,
      top: 20,
      backgroundColor: darken(theme.palette.primary.main, 0.2),
    },
    sideBar: {
      position: 'absolute',
      right: 0,
      top: 5,
      width: sidebarWidth,
      overflow: 'auto',
      maxHeight: 'calc(100vh - 50px)',
      zIndex: 5000,
    },
    content: {
      position: 'relative',
    },
  })
);

interface Props {
  setPageTitle: Function;
}

interface SVGData {
  width: number;
  height: number;
  xml: string;
  loaded: Date;
}

/**
 * Displays a chosen landscape as interactive SVG
 *
 * @param setLocateFunction function to use to find an item. make sure to pass an anon func returning the actually used function
 * @param setPageTitle can be used to set the page title in parent state
 */
const Map: React.FC<Props> = ({ setPageTitle }) => {
  const classes = useStyles();
  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});
  const [data, setData] = useState<SVGData | null>(null);
  const [sidebarContent, setSidebarContent] = useState<Element | Element[] | null>([]);
  const [renderWithTransition, setRenderWithTransition] = useState(false);
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);
  const [visualFocus, setVisualFocus] = useState<string | null>(null);
  const [isFirstRender, setIsFirstRender] = useState(true);
  const [isZoomed, setIsZoomed] = useState<boolean>(false);

  const locateFunctionContext = useContext(LocateFunctionContext);
  const landscapeContext = useContext(LandscapeContext);

  const locateComponent = useCallback(
    (fullyQualifiedItemIdentifier: string) => {
      const element = document.getElementById(fullyQualifiedItemIdentifier);
      if (element) {
        let dataX = Number(element.getAttribute('data-x'));
        let dataY = Number(element.getAttribute('data-y'));
        if (dataX && dataY && data) {
          setValue(setPointOnViewerCenter(value, dataX, dataY, 0.5));
          setRenderWithTransition(true);
          setHighlightElement(element);
          setIsZoomed(true);
        }
      }
    },
    [data, value]
  );

  const onItemClick = (e: MouseEvent<HTMLElement>) => {
    const fqi = e.currentTarget.getAttribute('data-identifier');
    setVisualFocus(fqi);

    if (fqi && landscapeContext.landscape) {
      let item = getItem(landscapeContext.landscape, fqi);
      if (item) {
        const item1 = (
          <Item
            fullyQualifiedItemIdentifier={item.fullyQualifiedIdentifier}
            key={`item_${item.fullyQualifiedIdentifier}_${Math.random()}`}
          />
        );
        // @ts-ignore
        setSidebarContent(item1);
      }
    }
  };

  const onGroupClick = (e: MouseEvent<HTMLElement>) => {
    const fqi = e.currentTarget.getAttribute('data-identifier');
    setVisualFocus(fqi);

    if (fqi && landscapeContext.landscape) {
      let group = getGroup(landscapeContext.landscape, fqi);
      if (group) {
        // @ts-ignore
        setSidebarContent(<Group group={group} key={`group_${fqi}_${Math.random()}`} />);
      }
    }
  };

  const onRelationClick = (e: MouseEvent<HTMLElement>) => {
    if (!landscapeContext.landscape) return;

    const dataSource = e.currentTarget.getAttribute('data-source');
    const fqi = e.currentTarget.getAttribute('data-identifier');
    setVisualFocus(fqi);

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
      let centerY = minY + (Math.max(sourceY, targetY) - minY) / 2;
      setValue(setPointOnViewerCenter(value, centerX, centerY, 0.3));
      setIsZoomed(true);
    }

    if (source && target && dataTarget) {
      const relId = source.fullyQualifiedIdentifier + ';' + dataTarget;
      let relation = source.relations[relId];
      const mapRelation = (
        <MapRelation
          relation={relation}
          source={source}
          target={target}
          key={`relation_${relId}_${Math.random()}`}
        />
      );
      // @ts-ignore
      setSidebarContent(mapRelation);
    }
  };

  const loadMap = useCallback(() => {
    if (!landscapeContext.identifier) {
      return;
    }
    const route = withBasePath(`/render/${landscapeContext.identifier}/map.svg`);
    get(route).then((svg) => {
      const parser = new DOMParser();
      const doc: any = parser.parseFromString(svg, 'image/svg+xml');
      const width = doc.firstElementChild.width.baseVal.value;
      const height = doc.firstElementChild.height.baseVal.value;
      setData({ width: width, height: height, xml: svg, loaded: new Date() });
    });
  }, [landscapeContext.identifier, setData]);

  /**
   * apply assessment values to all map components
   */
  const applyAssessment = useCallback(() => {
    if (!landscapeContext.assessment) return;
    Object.keys(landscapeContext.assessment?.results).forEach((key) => {
      const node = document.querySelector(`[data-identifier='${key}'] .assessment`);
      if (node) {
        const assessmentSummary = landscapeContext.getAssessmentSummary(key);
        if (assessmentSummary) {
          node.classList.remove('UNKNOWN', 'GREEN', 'YELLOW', 'ORANGE', 'RED', 'BROWN');
          node.classList.add(assessmentSummary?.status);
        }
      }
    });
  }, [landscapeContext.getAssessmentSummary, landscapeContext.assessment]);

  /**
   * on identifier change, load map
   */
  useEffect(() => {
    loadMap();
    setSidebarContent(null);
    applyAssessment();
  }, [landscapeContext.identifier, loadMap, setSidebarContent, applyAssessment]);

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
   * (re)paints the visual focus (also when the svg reloads)
   */
  useEffect(() => {
    if (!visualFocus) return;

    const currentSelected = document.getElementsByClassName('selected');
    Array.from(currentSelected).forEach((el) => {
      el.classList.remove('selected');
      el.classList.add('unselected');
    });

    const current = document.querySelectorAll("[data-identifier='" + visualFocus + "']");
    Array.from(current).forEach((vf) => {
      vf.classList.add('selected');
      vf.classList.remove('unselected');
    });
  }, [visualFocus, data]);

  /**
   * Reload map on notification messages.
   */
  useEffect(() => {
    if (!landscapeContext.mapChanges) return;
    console.debug('reloading map after map change', landscapeContext.mapChanges);
    loadMap();
    applyAssessment();
  }, [landscapeContext.mapChanges, loadMap, applyAssessment]);

  /**
   * Apply values on assessment change.
   */
  useEffect(() => {
    applyAssessment();
  }, [landscapeContext.assessment, applyAssessment]);

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
      <div className={classes.content}>
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
              <ZoomOutIcon />
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
                height={window.innerHeight - 50}
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
                onChangeTool={() => {
                  /* disabled */
                }}
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
        <div className={classes.sideBar}>{sidebarContent}</div>
      </div>
    );
  }
  return <span>loading...</span>;
};

export default Map;
