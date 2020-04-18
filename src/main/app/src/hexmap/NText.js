import React, { Component } from 'react';
import PropTypes from 'prop-types';

class NText extends Component {
  static propTypes = {
    item: PropTypes.object,
    x: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    y: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    className: PropTypes.string,
    width: PropTypes.number,
    onClick: PropTypes.func,
  };

  render() {
    const { item, x, y, className, width, onClick } = this.props;
    return (
      <text
        x={x || 0}
        y={y ? y : '0.3em'}
        width={width}
        className={className}
        textAnchor='middle'
        onClick={onClick}
      >
        {item.name}
      </text>
    );
  }
}

export default NText;
