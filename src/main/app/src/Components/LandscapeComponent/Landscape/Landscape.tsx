import React, { useState, useEffect, useContext, ReactElement } from 'react';

import { ReactSvgPanZoomLoader, SvgLoaderSelectElement } from 'react-svg-pan-zoom-loader';
import { ReactSVGPanZoom, TOOL_AUTO, Tool, Value } from 'react-svg-pan-zoom';
import { useParams } from 'react-router-dom';

import LandscapeItem from '../Item/LandscapeItem';
import Command from '../../CommandComponent/Command';
import GenericModal from '../../ModalComponent/GenericModal';
import LandscapeContext from '../../../Context/Landscape.context';
import CommandContext from '../../../Context/Command.context';

import './Landscape.scss';
import { ILandscape } from '../../../interfaces';

const Landscape: React.FC = () => {
  const [tool, setTool] = useState<Tool>(TOOL_AUTO);

  // It wants a value or null but if we defined it as null it throws an error that shouldn't use null
  // In their own documentation, they initialize it with {}, but that will invoke a typescript error
  // @ts-ignore
  const [value, setValue] = useState<Value>({});
  const [modalContent, setModalContent] = useState<string | ReactElement | null>(null);
  const [landscape, setLandscape] = useState<ILandscape | null>(null);
  const [reloadLandscape, setReloadLandscape] = useState<boolean>(false);

  const landscapeContext = useContext(LandscapeContext);
  const commandContext = useContext(CommandContext);
  const { identifier } = useParams();

  useEffect(() => {
    const index = landscapeContext.landscapes.findIndex(i => i.identifier === identifier);
    setLandscape(landscapeContext.landscapes[index]);
  }, [identifier, landscapeContext.landscapes, reloadLandscape]);

  const reloadLandscapes = async () => {
    await fetch(process.env.REACT_APP_BACKEND_URL + '/api/')
      .then(response => {
        return response.json();
      })
      .then(json => {
        landscapeContext.landscapes = json;
        commandContext.message = 'Loaded landscapes.';
        setReloadLandscape(!reloadLandscape);
      });
  };

  const onItemClick = (e: any) => {
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
              onChangeTool={newTool => setTool(newTool)}
              value={value}
              onChangeValue={newValue => setValue(newValue)}
            >
              <svg width={1000} height={1000}>
                {content}
              </svg>
            </ReactSVGPanZoom>
            <Command />
          </div>
        )}
      />
    );
  }

  return (
    <div className='landscapeError'>
      <span className='error'>No Landscapes loaded :(</span> <br />
      <button className='reload' onClick={reloadLandscapes}>
        Reload Landscapes
      </button>
      <Command />
    </div>
  );
};

export default Landscape;
