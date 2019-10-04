import React, {Component} from 'react';
import PropTypes from 'prop-types';
import classNames from 'classnames';
import {Text, Pattern, Path, Hex, HexUtils} from 'react-hexgrid';


class Nexagon extends Component {
    static propTypes = {
        q: PropTypes.number.isRequired,
        r: PropTypes.number.isRequired,
        s: PropTypes.number.isRequired,
        fill: PropTypes.string,
        cellStyle: PropTypes.oneOfType([
            PropTypes.string,
            PropTypes.object
        ]),
        className: PropTypes.string,
        data: PropTypes.object,
        onClick: PropTypes.func,
        children: PropTypes.node
    };

    static contextTypes = {
        layout: PropTypes.object, // TODO Shape
        points: PropTypes.string
    };

    constructor(props, context) {
        super(props, context);
        const {q, r, s} = props;
        const {layout} = context;
        const hex = new Hex(q, r, s);
        const pixel = HexUtils.hexToPixel(hex, layout);
        this.state = {hex, pixel};
    }

    // TODO Refactor to reduce duplicate
    componentWillReceiveProps(nextProps) {
        const {q, r, s} = nextProps;
        const {layout} = this.context;
        const hex = new Hex(q, r, s);
        const pixel = HexUtils.hexToPixel(hex, layout);
        this.setState({hex, pixel});
    }

    onClick(e) {
        if (this.props.onClick) {
            this.props.onClick(e, this);
        }
    }

    render() {
        const {fill, cellStyle, className} = this.props;
        const {points} = this.context;
        const {hex, pixel} = this.state;
        const fillId = (fill) ? `url(#${fill})` : null;
        return (
            <g
                className={classNames('hexagon-group', className)}
                transform={`translate(${pixel.x}, ${pixel.y})`}
                onClick={e => this.onClick(e)}
            >
                <g className="hexagon">
                    <polygon points={points} fill={fillId} style={cellStyle}/>
                    {this.props.children}
                </g>
            </g>
        );
    }
}

export default Nexagon;