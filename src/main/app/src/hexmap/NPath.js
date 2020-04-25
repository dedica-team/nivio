import React, { Component } from 'react';
import PropTypes from 'prop-types';

import { svgPathProperties } from 'svg-path-properties';

class NPath extends Component {
  static propTypes = {
    fill: PropTypes.string,
    tilePath: PropTypes.object,
    layout: PropTypes.object,
    realtion: PropTypes.object,
  };
  static contextTypes = {
    layout: PropTypes.object,
  };

  getPoints() {
    const { tilePath } = this.props;
    const { layout } = this.context;
    return tilePath.getPoints(layout);
  }

  render() {
    const { fill, relation } = this.props;
    const fillId = fill ? `#${fill}` : null;
    let path = this.getPoints();

    if (relation.type === 'PROVIDER') {
      return <path d={path} stroke={fillId} />;
    }

    const properties = new svgPathProperties(path);
    const separation = 15;
    let markers = [];
    for (var j = 0; j < Math.floor(properties.getTotalLength() / separation); j++) {
      var pos = properties.getPropertiesAtLength(separation * j);
      markers.push(this.marker(pos, fillId));
    }

    return markers;
  }

  marker(pos, fillId) {
    let degrees = (Math.atan(pos.tangentY / pos.tangentX) * 180) / Math.PI;
    if (pos.tangentX < 0 && pos.tangentY > 0)
      //TODO check
      degrees += 180;

    let transform =
      'translate(' + Math.floor(pos.x) + ' ' + pos.y + ') rotate(' + degrees + ' 0 0)';
    return (
      <text x='-10' y='0' fill={fillId} width='10' height='10' transform={transform}>
        &#10148;
      </text>
    );
  }
}

export default NPath;
