import React, { useState, useEffect, useCallback, ReactElement } from 'react';

import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import { ReactSVGPanZoom, TOOL_AUTO, Tool, Value } from 'react-svg-pan-zoom';
import { useParams } from 'react-router-dom';

import LandscapeItem from '../Item/LandscapeItem';
import GenericModal from '../../ModalComponent/GenericModal';
import { getLandscapeByIdentifier } from '../../../utils/APIClient';

import './Landscape.scss';
import { ILandscape } from '../../../interfaces';

/**
 * Displays a choosen landscape as interactive SVG
 */
const Landscape: React.FC = () => {
  const [tool, setTool] = useState<Tool>(TOOL_AUTO);

  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});
  const [modalContent, setModalContent] = useState<string | ReactElement | null>(null);
  const [landscape, setLandscape] = useState<ILandscape | null>(null);
  const [loadLandscape, setLoadLandscape] = useState<boolean>(true);

  const { identifier } = useParams();

  const getLandscape = useCallback(async () => {
    if (loadLandscape && identifier) {
      setLandscape(await getLandscapeByIdentifier(identifier));
      setLoadLandscape(false);
    }
  }, [loadLandscape, identifier]);

  useEffect(() => {
    getLandscape();
  }, [getLandscape]);

  const onItemClick = (e: any) => {
    setModalContent(<LandscapeItem element={e.target.parentElement} />);
  };

  if (landscape) {
    let data = process.env.REACT_APP_BACKEND_URL + '/render/' + landscape.identifier + '/map.svg';
    return (
      <ReactSvgPanZoomLoader
        src={data}
        proxy={
          <>
            <SvgLoaderSelectElement selector='.label' onClick={onItemClick} />
          </>
        }
        render={(content: ReactElement[]) => (
          <div className='landscapeContainer'>
            <GenericModal modalContent={modalContent} />
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
          </div>
        )}
      />
    );
  }

  return (
    <div className='landscapeError'>
      <span className='error'>Loading...</span>
    </div>
  );
};

export default Landscape;
