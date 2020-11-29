/**
 * Alternative version of ReactSvgPanZoomLoader which allows using preloaded XML.
 *
 */
import React from "react";
import PropTypes from 'prop-types'
import { SvgLoader } from 'react-svgmt'

interface ReactSvgPanZoomLoaderXMLProps {
    xml: PropTypes.string.isRequired,
    render: PropTypes.func.isRequired,
    proxy: PropTypes.node
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