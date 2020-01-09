import React, {Component} from 'react';
import PropTypes from 'prop-types';
import {HexUtils} from 'react-hexgrid';

class NGroup extends Component {

    static propTypes = {
        group: PropTypes.object,
        cellStyle: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.object
        ])
    };

    static contextTypes = {
        layout: PropTypes.object
    };

    render() {
        const {group, cellStyle} = this.props;
        const {layout} = this.context;
        const fill = group.color;
        const fillId = (fill) ? `#${fill}` : null;
        const start = group.start;
        const end = group.end;
        const size = 40;
        let startPoint = HexUtils.hexToPixel(start, layout);
        let endPoint = HexUtils.hexToPixel(end, layout);
        let width = endPoint.x - startPoint.x + 4 * size;
        let height = endPoint.y - startPoint.y + 5 * size;
        return (
            <g>
                <rect x={startPoint.x - 2 * size} y={startPoint.y - 2 * size} rx="50" ry="50" width={width}
                      height={height}
                      style={cellStyle}
                      stroke={fillId} fill={fillId} className="group"/>
                <text x={startPoint.x - 2 * size + width / 2} y={startPoint.y + height - 3 * size} fill={fillId}
                      font-size="24" width={width} textAnchor={'middle'}>{group.name}</text>
            </g>
        );
    }
}

export default NGroup;