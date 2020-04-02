import React, { useState, useEffect, useContext } from 'react';

import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import { ReactSVGPanZoom, TOOL_AUTO } from 'react-svg-pan-zoom';
import { useParams } from 'react-router-dom';

import LandscapeItem from '../Item/LandscapeItem';
import Command from '../../CommandComponent/Command';
import GenericModal from '../../ModalComponent/GenericModal';
import LandscapeContext from '../../../Context/Landscape.context';

import './Landscape.scss';

const Landscape = () => {
  const [tool, setTool] = useState(TOOL_AUTO);
  const [value, setValue] = useState({});
  const [modalContent, setModalContent] = useState(null);
  const [landscape, setLandscape] = useState(null);

  const landscapeContext = useContext(LandscapeContext);
  const { identifier } = useParams();

  useEffect(() => {
    const index = landscapeContext.landscapes.findIndex(i => i.identifier === identifier);
    setLandscape(landscapeContext.landscapes[index]);
  }, [identifier, landscapeContext.landscapes]);

  const onItemClick = e => {
    setModalContent(
      <LandscapeItem
        host={process.env.REACT_APP_BACKEND_URL || 'localhost:8080'}
        element={e.target.parentElement}
      />
    );
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
        render={content => (
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
              onChangeTool={tool => setTool(tool)}
              value={value}
              onChangeValue={value => setValue(value)}
            >
              <svg>{content}</svg>
            </ReactSVGPanZoom>
            <Command />
          </div>
        )}
      />
    );
  }

  return (
    <div>
      No Landscapes loaded :( <Command />
    </div>
  );
};

export default Landscape;
