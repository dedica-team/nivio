import React, { MouseEvent, ReactElement, useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import {
  fitSelection,
  fitToViewer,
  ReactSVGPanZoom,
  setPointOnViewerCenter,
  Tool,
  TOOL_AUTO,
  Value,
} from 'react-svg-pan-zoom';

import { CSSTransition } from 'react-transition-group';

import './Map.scss';
import Item from '../Modals/Item/Item';

import Slider from '../../Slider/Slider';
import MapRelation from './MapRelation/MapRelation';
import Search from '../Search/Search';
import { withBasePath } from '../../../utils/API/BasePath';
import Assessment from '../Modals/Assessment/Assessment';
import { get } from '../../../utils/API/APIClient';

interface Props {
  identifier: string;
}

interface SVGData {
  width: number;
  height: number;
  content: any;
  url: string;
}

/**
 * Displays a choosen landscape as interactive SVG
 * @param identifier Landscape Identifier
 * @param onItemClick Handler for our label click
 */
const Map: React.FC<Props> = () => {
  const [tool, setTool] = useState<Tool>(TOOL_AUTO);

  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});

  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
  const [data, setData] = useState<SVGData | null>(null);
  const [renderWithTransition, setRenderWithTransition] = useState(false);
  const [highlightElement, setHighlightElement] = useState<Element | HTMLCollection | null>(null);
  const { identifier } = useParams<{ identifier: string }>();

  const [isFirstRender, setIsFirstRender] = useState(true);

  const findItem = (fullyQualifiedItemIdentifier: string) => {
    const element = document.getElementById(fullyQualifiedItemIdentifier);
    if (element) {
      let dataX = element.getAttribute('data-x');
      let dataY = element.getAttribute('data-y');
      if (dataX && dataY) {
        const shift: number = 200; //shift all a bit to left, since on the right is the sidebar
        const x = parseFloat(dataX) + shift;
        const y = parseFloat(dataY) + shift / 2;
        setValue(setPointOnViewerCenter(value, x, y, 1));
        setRenderWithTransition(true);
        setHighlightElement(element);
      }
    }
  };

  const onItemClick = (e: MouseEvent<HTMLElement>) => {
    const fullyQualifiedItemIdentifier = e.currentTarget.getAttribute('data-identifier');
    if (fullyQualifiedItemIdentifier) {
      setSliderContent(
        <Item
          fullyQualifiedItemIdentifier={fullyQualifiedItemIdentifier}
          findItem={findItem}
          onAssessmentClick={onAssessmentClick}
        />
      );
      setShowSlider(true);
    }
  };

  const onAssessmentHeaderClick = (fullyQualifiedItemIdentifier: string) => {
    setSliderContent(
      <Item
        fullyQualifiedItemIdentifier={fullyQualifiedItemIdentifier}
        findItem={findItem}
        onAssessmentClick={onAssessmentClick}
      />
    );
    setShowSlider(true);
  };

  const onAssessmentClick = (fullyQualifiedItemIdentifier: string) => {
    setSliderContent(
      <Assessment
        fullyQualifiedIdentifier={fullyQualifiedItemIdentifier}
        findItem={onAssessmentHeaderClick}
        isGroup={false}
      />
    );
    setShowSlider(true);
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
      setSliderContent(
        <MapRelation
          sourceIdentifier={dataSource}
          targetIdentifier={dataTarget}
          type={dataType}
          findItem={findItem}
        />
      );
      setShowSlider(true);
    }
  };

  const closeSlider = () => {
    setShowSlider(false);
  };

  useEffect(() => {
    // TODO this loads the svg only to obtain width and height, svgloader loads it again to place it in the DOM
    let route = withBasePath(`/render/${identifier}/map.svg`);
    get(route).then((svg) => {
      const parser = new DOMParser();
      const doc: any = parser.parseFromString(svg, 'image/svg+xml');
      const width = doc.firstElementChild.width.baseVal.value;
      const height = doc.firstElementChild.height.baseVal.value;
      setData({ width: width, height: height, content: svg , url: route});
    });
  }, [identifier]);

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
        <Search findItem={findItem} />
        <CSSTransition
          in={showSlider}
          timeout={{ enter: 0, exit: 1000, appear: 1000 }}
          appear
          unmountOnExit
          classNames='slider'
        >
          <Slider sliderContent={sliderContent} closeSlider={closeSlider} />
        </CSSTransition>
        <ReactSvgPanZoomLoader
          src={data.url}
          proxy={
            <>
              <SvgLoaderSelectElement selector='.label' onClick={onItemClick} />
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
      </div>
    );
  }
  return <span>loading...</span>;
};

export default Map;
