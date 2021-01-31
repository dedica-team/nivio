import { Value } from 'react-svg-pan-zoom';

const MapUtils = {
  /**
   * Calculates the proper center coordinates to focus an item.
   *
   * @param value svg viewer context
   * @param dataX x coordinate attribute of element
   * @param dataY y coordinate attribute of element
   */
  getCenterCoordinates: (value: Value, dataX: string, dataY: string): { x: number; y: number } => {
    const drawerWidth = 320;
    const halfWidth = value.viewerWidth / 2;
    let x = parseFloat(dataX);
    if (x < halfWidth) {
      x = halfWidth - drawerWidth;
    }
    if (x > value.SVGWidth - halfWidth) {
      x = value.SVGWidth - halfWidth + drawerWidth;
    }

    const halfHeight = value.viewerHeight / 2;
    let y = parseFloat(dataY);
    if (y < halfHeight) {
      y = halfHeight;
    }
    if (y > value.SVGHeight - halfHeight) {
      y = value.SVGHeight - halfHeight;
    }
    return { x: x, y: y };
  },
};

export default MapUtils;
