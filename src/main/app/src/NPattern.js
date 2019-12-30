import React, { Component } from 'react';
import PropTypes from 'prop-types';


class Pattern extends Component {
    static propTypes = {
        id: PropTypes.string.isRequired,
        link: PropTypes.string.isRequired,
        size: PropTypes.object
    };

    render() {
        const { id, link, size } = this.props;

        return (
            <defs>
                <pattern id={id} patternUnits="objectBoundingBox" x={0} y={0} width={size.x} height={size.y}>
                    <rect height="100" width="100" fill="white"/>
                    <image xlinkHref={link} x={10} y={10} width={size.x*2} height={size.y*2} />
                </pattern>
            </defs>
        );
    }
}

export default Pattern;