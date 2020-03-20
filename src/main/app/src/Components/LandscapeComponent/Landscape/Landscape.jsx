/*
    TODO: REFACTORING
*/

import React, {useState, useEffect, useContext, ReactElement} from "react";

import {ReactSvgPanZoomLoader, SvgLoaderSelectElement} from "react-svg-pan-zoom-loader";
import {ReactSVGPanZoom, TOOL_AUTO} from "react-svg-pan-zoom";

import ItemModalContent from "../../ItemModalContentComponent/ItemModalContent";
import Command from '../../CommandComponent/Command';


const Landscape = ({landscape}) => {
    const [tool, setTool] = useState(TOOL_AUTO);
    const [value, setValue] = useState();
    const [modalContent, setModalContent] = useState(null);

    let Viewer = null;


    const onItemClick = (e) => {
        setModalContent(<ItemModalContent host={process.env.REACT_APP_BACKEND_URL || "localhost:8080"}
                                        element={e.target.parentElement}
                                        closeFn={onModalClose}/>);
    };

    const onModalClose = () => {
        setModalContent(null);
    };

    /*if (landscape) {
        let data =process.env.REACT_APP_BACKEND_URL + '/render/' + landscape.identifier + '/map.svg';
        return <ReactSvgPanZoomLoader src={data} proxy={
            <>
                <SvgLoaderSelectElement selector=".label" onClick={onItemClick}/>
            </>
        } render={(content) => (
            <div>
                <GenericModal />
                <ReactSVGPanZoom key={'panzoom'}
                                 width={window.innerWidth * 0.95} height={window.innerHeight * 0.95}
                                 background={'transparent'}
                                 miniatureProps={{position: 'none', background: '#616264', width: 100, height: 80}} 
                                 toolbarProps={{position: 'none'}}
                                 detectAutoPan={false}
                                 ref={Viewer => Viewer1 = Viewer}
                                 tool={tool} onChangeTool={tool => setTool(tool)}
                                 value={value} onChangeValue={value => setValue(value)}>
                    <svg>
                        {content}
                    </svg>
                </ReactSVGPanZoom>
            </div>
        )}/>
    }*/

    return <div>No Landscape :( <Command/></div>
};

export default Landscape;