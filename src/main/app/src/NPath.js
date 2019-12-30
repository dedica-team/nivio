import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {HexUtils} from 'react-hexgrid';

class NPath extends Component {
    static propTypes = {
        fill: PropTypes.string,
        tiles: PropTypes.array,
        layout: PropTypes.object
    };
    static contextTypes = {
        layout: PropTypes.object
    };

    getPoints() {
        const { tiles } = this.props;
        const { layout } = this.context;

        //for (let i=0; i<=distance; i++) {
         //   intersects.push(HexUtils.round(HexUtils.hexLerp(start, end, step * i)));
        //}

        // Construct Path points out of all the intersecting hexes (e.g. M 0,0 L 10,20, L 30,20)
        let points = 'M';
        points += tiles.map(hex => {
            let p = HexUtils.hexToPixel(hex, layout);
            return ` ${p.x},${p.y} `;
        }).join('L');

        return points;
    }

    render() {
        const {fill} = this.props;
        const fillId = (fill) ? `#${fill}` : null;
        return (
            <path d={this.getPoints()} stroke={fillId}/>
        );
    }
}

export default NPath;