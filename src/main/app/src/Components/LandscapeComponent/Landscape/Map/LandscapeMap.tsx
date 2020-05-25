import React, { useState, ReactElement } from 'react';

import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import { ReactSVGPanZoom, TOOL_AUTO, Tool, Value } from 'react-svg-pan-zoom';

import './LandscapeMap.scss';

interface Props {
  identifier: string;
  closeSlider(): void;
  onItemClick(e: any): void;
}

/**
 * Displays a choosen landscape as interactive SVG
 */
const Landscape: React.FC<Props> = ({ identifier, onItemClick, closeSlider }) => {
  const [tool, setTool] = useState<Tool>(TOOL_AUTO);

  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});

  let data = process.env.REACT_APP_BACKEND_URL + '/render/' + identifier + '/map.svg';
  return (
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
          width={window.innerWidth * 0.95}
          height={window.innerHeight * 0.95}
          background={'transparent'}
          miniatureProps={{
            position: 'none',
            background: '#616264',
            width: 100,
            height: 80,
          }}
          toolbarProps={{ position: 'none' }}
          detectAutoPan={false}
          tool={tool}
          onChangeTool={(newTool) => setTool(newTool)}
          value={value}
          onChangeValue={(newValue) => setValue(newValue)}
        >
          <svg width={1000} height={1000}>
            {content}
          </svg>
        </ReactSVGPanZoom>
      )}
    />
  );
};

export default Landscape;
