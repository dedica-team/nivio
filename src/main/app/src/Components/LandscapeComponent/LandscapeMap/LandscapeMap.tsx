import React, { useState, useEffect, ReactElement, MouseEvent } from 'react';
import { useParams } from 'react-router-dom';

import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import { ReactSVGPanZoom, TOOL_AUTO, Tool, Value, fitSelection } from 'react-svg-pan-zoom';

import { CSSTransition } from 'react-transition-group';

import './LandscapeMap.scss';
import LandscapeItem from '../LandscapeItem/LandscapeItem';

import Slider from '../../SliderComponent/Slider';

interface Props {
  identifier: string;
}

/**
 * Displays a choosen landscape as interactive SVG
 * @param identifier Landscape Identifier
 * @param onItemClick Handler for our label click
 */
const Landscape: React.FC<Props> = () => {
  const [tool, setTool] = useState<Tool>(TOOL_AUTO);

  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});

  const [sliderContent, setSliderContent] = useState<string | ReactElement | null>(null);
  const [showSlider, setShowSlider] = useState(false);
  const [data, setData] = useState('');
  const [renderWithTransition, setRenderWithTransition] = useState(false);
  const { identifier } = useParams();

  const findItem = (fullyQualifiedItemIdentifier: string) => {
    const element = document.getElementById(fullyQualifiedItemIdentifier);
    if (element) {
      let dataX = element.getAttribute('data-x');
      let dataY = element.getAttribute('data-y');
      if (dataX && dataY) {
        const x = parseFloat(dataX) - 500;
        const y = parseFloat(dataY) - 100;
        setValue(fitSelection(value, x, y, window.innerWidth, window.innerHeight * 0.92));
        setRenderWithTransition(true);
      }

      element.classList.add('highlight');
      setTimeout(() => {
        element.classList.remove('highlight');
        setRenderWithTransition(false);
      }, 3000);
    }
  };

  const onItemClick = (e: MouseEvent<HTMLElement>) => {
    const fullyQualifiedItemIdentifier = e.currentTarget.getAttribute('data-identifier');
    if (fullyQualifiedItemIdentifier) {
      setSliderContent(
        <LandscapeItem
          fullyQualifiedItemIdentifier={fullyQualifiedItemIdentifier}
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
    setData(process.env.REACT_APP_BACKEND_URL + '/render/' + identifier + '/map.svg');
  }, [identifier]);

  if (data !== '') {
    return (
      <div className='landscapeContainer'>
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
          src={data}
          proxy={
            <>
              <SvgLoaderSelectElement selector='.label' onClick={onItemClick} />
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
              <svg width={window.innerWidth} height={window.innerHeight * 0.92}>
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

export default Landscape;
