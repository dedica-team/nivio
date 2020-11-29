/**
 * Alternative version of ReactSvgPanZoomLoader which allows using preloaded XML.
 *
 */
import React from "react";
// @ts-ignore
import { SvgLoader } from 'react-svgmt'

interface ReactSvgPanZoomLoaderXMLProps {
    xml: string;
    render: any;
    proxy: any;
}

const ReactSvgPanZoomLoaderXML = (props : ReactSvgPanZoomLoaderXMLProps) => {
    return (
        <div>
            {props.render(
                    <SvgLoader svgXML={props.xml}>
                        {props.proxy}
                        </SvgLoader>
                )}
        </div>
    )
}

ReactSvgPanZoomLoaderXML.defaultProps = {
    proxy: ""
}

export {ReactSvgPanZoomLoaderXML}