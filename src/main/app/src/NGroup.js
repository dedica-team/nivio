import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {HexUtils} from 'react-hexgrid';

class NGroup extends Component {

    static propTypes = {
        fill: PropTypes.string,
        start: PropTypes.object,
        end: PropTypes.object,
        cellStyle: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.object
        ])
    };

    static contextTypes = {
        layout: PropTypes.object
    };

    render() {
        const {start, end, fill, cellStyle} = this.props;
        const {layout} = this.context;
        const fillId = (fill) ? `#${fill}` : null;
        const size = 40;
        let startPoint = HexUtils.hexToPixel(start, layout);
        let endPoint = HexUtils.hexToPixel(end, layout);
        let width = endPoint.x - startPoint.x + 4 * size;
        let height = endPoint.y - startPoint.y + 4*size;
        return (
            <rect x={startPoint.x - 2 * size} y={startPoint.y - 2 * size} rx="50" ry="50" width={width} height={height}
                  style={cellStyle}
                  stroke={fillId} fill={fillId} className="group"/>
        );
    }
}

export default NGroup;