import React, { Component } from 'react';
import PropTypes from 'prop-types';

class NPath extends Component {
    static propTypes = {
        fill: PropTypes.string,
        tilePath: PropTypes.object,
        layout: PropTypes.object
    };
    static contextTypes = {
        layout: PropTypes.object
    };

    getPoints() {
        const { tilePath } = this.props;
        const { layout } = this.context;
        return tilePath.getPoints(layout);
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