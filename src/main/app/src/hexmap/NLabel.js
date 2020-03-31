import React, { Component } from 'react';
import PropTypes from 'prop-types';
import NText from './NText';

class NLabel extends Component {
  static propTypes = {
    item: PropTypes.object,
    x: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    y: PropTypes.oneOfType([PropTypes.string, PropTypes.number]),
    className: PropTypes.string,
    width: PropTypes.number,
    size: PropTypes.number,
    padding: PropTypes.number,
  };

  constructor() {
    super();
    this.state = { showDetails: false };
  }

  itemPopup(e) {
    this.setState({ showDetails: !this.state.showDetails });
  }

  render() {
    const { size, item, padding, width } = this.props;
    let showDetails = this.state.showDetails;
    let style = {
      stroke: item.status,
    };
    let detailsStyle = {
      display: showDetails ? 'block' : 'none',
    };

    return (
      <g className={'label'}>
        <rect
          x={size + padding}
          y={-10}
          rx='10'
          ry='10'
          fill={'white'}
          width={width}
          height={size / 2}
          style={style}
        />
        <NText
          key={item.identifier}
          x={size + padding + width / 2}
          y={5}
          width={width}
          item={item}
          onClick={e => this.itemPopup(e, this)}
        />

        <foreignObject
          width={width}
          height='220'
          y={padding}
          x={size + padding}
          style={detailsStyle}
        >
          <div className='details'>
            {item.landscapeItem.description && (
              <div>
                "{item.landscapeItem.description}"<br />
              </div>
            )}
            {item.landscapeItem.owner && <div>Owner: {item.landscapeItem.owner}</div>}
            {item.landscapeItem.team && <div>Team: {item.landscapeItem.team}</div>}
            {item.landscapeItem.contact && <div>Contact: {item.landscapeItem.contact}</div>}
            {item.landscapeItem.software && <div>Software: {item.landscapeItem.software}</div>}
            {item.landscapeItem.version && <div>Version: {item.landscapeItem.version}</div>}
            {item.landscapeItem.lifecycle && <div>Lifecycle: {item.landscapeItem.lifecycle}</div>}
          </div>
        </foreignObject>
      </g>
    );
  }
}

export default NLabel;
