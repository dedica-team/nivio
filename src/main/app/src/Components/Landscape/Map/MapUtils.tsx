/**
 *
 * @param lowerBound viewbox x or y
 * @param coordinate original item x or y coordinate on the map
 * @param dimension total width or height
 */
export const getCorrected = (lowerBound: number, coordinate: number, dimension: number): number => {
  let correction = 0;
  //if in first half we subtract the viewbox offset
  if (coordinate < dimension / 2) {
    if (lowerBound < 0) correction = (lowerBound / 2) * -1;
    //if in second half we add some viewbox offset depending on the relative position to center
  } else {
    let factor = (dimension - coordinate) / dimension;
    correction = (lowerBound / 2) * -1 * factor;
  }
  return coordinate + correction;
};

/**
 * This is a naive approach to work around #438
 *
 */
export const getApproximateCenterCoordinates = (
  viewBox: SVGRect,
  width: number,
  height: number,
  dataX: number,
  dataY: number
): { x: number; y: number } => {
  dataX = getCorrected(viewBox.x, dataX, width);
  dataY = getCorrected(viewBox.y, dataY, height);
  return { x: dataX, y: dataY };
};
