import {
  applyToPoint,
  fromObject,
  identity,
  inverse,
  Matrix,
  scale,
  transform,
  translate,
} from 'transformation-matrix';
import * as React from 'react';
import { Props } from './props';

const VERSION = 3;
export const MODE_IDLE = 'idle';
export const MODE_PANNING = 'panning';
export const MODE_ZOOMING = 'zooming';

export const TOOL_AUTO = 'auto';
export const TOOL_NONE = 'none';
export const TOOL_PAN = 'pan';
export const TOOL_ZOOM_IN = 'zoom-in';
export const TOOL_ZOOM_OUT = 'zoom-out';

export const POSITION_NONE = 'none';
export const POSITION_TOP = 'top';
export const POSITION_RIGHT = 'right';
export const POSITION_BOTTOM = 'bottom';
export const POSITION_LEFT = 'left';

export const ACTION_ZOOM = 'zoom';
export const ACTION_PAN = 'pan';

export const ALIGN_CENTER = 'center';
export const ALIGN_LEFT = 'left';
export const ALIGN_RIGHT = 'right';
export const ALIGN_TOP = 'top';
export const ALIGN_BOTTOM = 'bottom';
export const ALIGN_COVER = 'cover';

export const INITIAL_VALUE = {};
export const DEFAULT_MODE = MODE_IDLE;

export type Mode = typeof MODE_IDLE | typeof MODE_PANNING | typeof MODE_ZOOMING;

export interface Value {
  version: 2;
  mode: Mode;
  focus: boolean;
  a: number;
  b: number;
  c: number;
  d: number;
  e: number;
  f: number;
  viewerWidth: number;
  viewerHeight: number;
  SVGWidth: number;
  SVGHeight: number;
  SVGMinX: number;
  SVGMinY: number;
  startX?: number | null;
  startY?: number | null;
  endX?: number | null;
  endY?: number | null;
  miniatureOpen: boolean;
  scaleFactorMin: number;
  scaleFactorMax: number;
}

export type Tool =
  | typeof TOOL_AUTO
  | typeof TOOL_NONE
  | typeof TOOL_PAN
  | typeof TOOL_ZOOM_IN
  | typeof TOOL_ZOOM_OUT;
export type ToolbarPosition =
  | typeof POSITION_NONE
  | typeof POSITION_TOP
  | typeof POSITION_RIGHT
  | typeof POSITION_BOTTOM
  | typeof POSITION_LEFT;

export interface ReactSVGPanZoom extends React.Component<Props> {
  pan(SVGDeltaX: number, SVGDeltaY: number): void;

  zoom(SVGPointX: number, SVGPointY: number, scaleFactor: number): void;

  fitSelection(
    selectionSVGPointX: number,
    selectionSVGPointY: number,
    selectionWidth: number,
    selectionHeight: number
  ): void;

  fitToViewer(): void;

  setPointOnViewerCenter(SVGPointX: number, SVGPointY: number, zoomLevel: number): void;

  reset(): void;

  zoomOnViewerCenter(scaleFactor: number): void;

  getValue(): Value;

  setValue(value: Value): void;

  getTool(): Tool;

  setTool(tool: Tool): void;
}

export interface Point {
  x: number;
  y: number;
}

export function isZoomLevelGoingOutOfBounds(value: Value, scaleFactor: number) {
  const { scaleFactor: curScaleFactor } = decompose(value);
  const lessThanScaleFactorMin =
    value.scaleFactorMin && curScaleFactor * scaleFactor < value.scaleFactorMin;
  const moreThanScaleFactorMax =
    value.scaleFactorMax && curScaleFactor * scaleFactor > value.scaleFactorMax;

  return (lessThanScaleFactorMin && scaleFactor < 1) || (moreThanScaleFactorMax && scaleFactor > 1);
}

export function zoom(
  value: Value,
  SVGPointX: number,
  SVGPointY: number,
  scaleFactor: number
): Value {
  if (isZoomLevelGoingOutOfBounds(value, scaleFactor)) {
    // Do not change translation and scale of value
    return value;
  }

  const matrix = transform(
    fromObject(value),
    translate(SVGPointX, SVGPointY),
    scale(scaleFactor, scaleFactor),
    translate(-SVGPointX, -SVGPointY)
  );

  return set(
    value,
    {
      mode: MODE_IDLE,
      ...matrix,
      startX: null,
      startY: null,
      endX: null,
      endY: null,
    },
    ACTION_ZOOM
  );
}

export function fitSelection(
  value: Value,
  selectionSVGPointX: number,
  selectionSVGPointY: number,
  selectionWidth: number,
  selectionHeight: number
): Value {
  let { viewerWidth, viewerHeight } = value;

  let scaleX = viewerWidth / selectionWidth;
  let scaleY = viewerHeight / selectionHeight;

  let scaleLevel = Math.min(scaleX, scaleY);

  const matrix = transform(
    scale(scaleLevel, scaleLevel), //2
    translate(-selectionSVGPointX, -selectionSVGPointY) //1
  );

  if (isZoomLevelGoingOutOfBounds(value, scaleLevel / value.d)) {
    // Do not allow scale and translation
    return set(value, {
      mode: MODE_IDLE,
      startX: null,
      startY: null,
      endX: null,
      endY: null,
    });
  }

  return set(
    value,
    {
      mode: MODE_IDLE,
      ...limitZoomLevel(value, matrix),
      startX: null,
      startY: null,
      endX: null,
      endY: null,
    },
    ACTION_ZOOM
  );
}

export function fitToViewer(value: Value, SVGAlignX = ALIGN_LEFT, SVGAlignY = ALIGN_TOP): Value {
  let { viewerWidth, viewerHeight, SVGMinX, SVGMinY, SVGWidth, SVGHeight } = value;

  let scaleX = viewerWidth / SVGWidth;
  let scaleY = viewerHeight / SVGHeight;
  let scaleLevel = Math.min(scaleX, scaleY);

  let scaleMatrix = scale(scaleLevel, scaleLevel);

  let translateX = -SVGMinX * scaleX;
  let translateY = -SVGMinY * scaleY;

  // after fitting, SVG and the viewer will match in width (1) or in height (2) or SVG will cover the container with preserving aspect ratio (0)
  if (scaleX < scaleY) {
    let remainderY = viewerHeight - scaleX * SVGHeight;

    //(1) match in width, meaning scaled SVGHeight <= viewerHeight
    switch (SVGAlignY) {
      case ALIGN_TOP:
        translateY = -SVGMinY * scaleLevel;
        break;

      case ALIGN_CENTER:
        translateY = Math.round(remainderY / 2) - SVGMinY * scaleLevel;
        break;

      case ALIGN_BOTTOM:
        translateY = remainderY - SVGMinY * scaleLevel;
        break;

      case ALIGN_COVER:
        scaleMatrix = scale(scaleY, scaleY); // (0) we must now match to short edge, in this case - height
        let remainderX = viewerWidth - scaleY * SVGWidth; // calculate remainder in the other scale

        translateX = SVGMinX + Math.round(remainderX / 2); // center by the long edge
        break;

      default:
      //no op
    }
  } else {
    let remainderX = viewerWidth - scaleY * SVGWidth;

    //(2) match in height, meaning scaled SVGWidth <= viewerWidth
    switch (SVGAlignX) {
      case ALIGN_LEFT:
        translateX = -SVGMinX * scaleLevel;
        break;

      case ALIGN_CENTER:
        translateX = Math.round(remainderX / 2) - SVGMinX * scaleLevel;
        break;

      case ALIGN_RIGHT:
        translateX = remainderX - SVGMinX * scaleLevel;
        break;

      case ALIGN_COVER:
        scaleMatrix = scale(scaleX, scaleX); // (0) we must now match to short edge, in this case - width
        let remainderY = viewerHeight - scaleX * SVGHeight; // calculate remainder in the other scale

        translateY = SVGMinY + Math.round(remainderY / 2); // center by the long edge
        break;

      default:
      //no op
    }
  }

  const translationMatrix = translate(translateX, translateY);

  const matrix: Matrix = transform(
    translationMatrix, //2
    scaleMatrix //1
  );

  if (isZoomLevelGoingOutOfBounds(value, scaleLevel / value.d)) {
    // Do not allow scale and translation
    return set(value, {
      mode: MODE_IDLE,
      startX: null,
      startY: null,
      endX: null,
      endY: null,
    });
  }

  return set(
    value,
    {
      mode: MODE_IDLE,
      ...limitZoomLevel(value, matrix),
      startX: null,
      startY: null,
      endX: null,
      endY: null,
    },
    ACTION_ZOOM
  );
}

export function zoomOnViewerCenter(value: Value, scaleFactor: number): Value {
  let { viewerWidth, viewerHeight } = value;
  let SVGPoint = getSVGPoint(value, viewerWidth / 2, viewerHeight / 2);
  return zoom(value, SVGPoint.x, SVGPoint.y, scaleFactor);
}

export function limitZoomLevel(value: Value, matrix: Matrix): Matrix {
  let scaleLevel = matrix.a;

  if (value.scaleFactorMin != null) {
    // limit minimum zoom
    scaleLevel = Math.max(scaleLevel, value.scaleFactorMin);
  }

  if (value.scaleFactorMax != null) {
    // limit maximum zoom
    scaleLevel = Math.min(scaleLevel, value.scaleFactorMax);
  }

  const newMatrix = Object.assign({}, matrix, {
    a: scaleLevel,
    d: scaleLevel,
  });
  return Object.freeze(newMatrix);
}

/**
 *
 * @param value
 * @param SVGPointX
 * @param SVGPointY
 * @param zoomLevel
 * @returns {Object}
 */
export function setPointOnViewerCenter(
  value: Value,
  SVGPointX: number,
  SVGPointY: number,
  zoomLevel: number
): Value {
  let { viewerWidth, viewerHeight } = value;

  console.log('viewer center', SVGPointX, SVGPointY, viewerWidth, viewerHeight);
  let matrix = transform(
    translate(-SVGPointX + viewerWidth / 2, -SVGPointY + viewerHeight / 2), //4
    translate(SVGPointX, SVGPointY) //3
  );
  matrix = transform(
    matrix,
    scale(zoomLevel, zoomLevel) //2
  );
  matrix = transform(
    matrix,
    translate(-SVGPointX, -SVGPointY) //1
  );

  return set(value, {
    mode: MODE_IDLE,
    ...matrix,
  });
}

/**
 *
 * @param value
 * @returns {Object}
 */
export function reset(value: Value) {
  return set(value, {
    mode: MODE_IDLE,
    ...identity(),
  });
}

/**
 * Obtain default value
 * @returns {Object}
 */
export function getDefaultValue(
  viewerWidth: number,
  viewerHeight: number,
  SVGMinX: number,
  SVGMinY: number,
  SVGWidth: number,
  SVGHeight: number,
  scaleFactorMin = null,
  scaleFactorMax = null
): Value {
  // @ts-ignore
  const init: Value = {};
  return set(init, {
    ...identity(),
    version: VERSION,
    mode: DEFAULT_MODE,
    focus: false,
    pinchPointDistance: null,
    prePinchMode: null,
    viewerWidth,
    viewerHeight,
    SVGMinX,
    SVGMinY,
    SVGWidth,
    SVGHeight,
    scaleFactorMin,
    scaleFactorMax,
    startX: null,
    startY: null,
    endX: null,
    endY: null,
    miniatureOpen: true,
    lastAction: null,
  });
}

/**
 * Change value
 * @param value
 * @param patch
 * @param action
 * @returns {Object}
 */
export function set(value: Value, patch: any, action: any = null): Value {
  value = Object.assign({}, value, patch, { lastAction: action });
  return Object.freeze(value);
}

/**
 * value valid check
 * @param value
 */
export function isValueValid(value: Value): boolean {
  return value !== null && typeof value === 'object' && value.hasOwnProperty('version');
}

/**
 * Export x,y coords relative to SVG
 * @param value
 * @param viewerX
 * @param viewerY
 * @returns {*|{x, y}|{x: number, y: number}}
 */
export function getSVGPoint(value: Value, viewerX: number, viewerY: number): PointObjectNotation {
  let matrix = fromObject(value);

  let inverseMatrix = inverse(matrix);
  return applyToPoint(inverseMatrix, { x: viewerX, y: viewerY });
}

interface Decomposed {
  scaleFactor: number;
  translationX: number;
  translationY: number;
}

/**
 * Decompose matrix from value
 * @param value
 * @returns {{scaleFactor: number, translationX: number, translationY: number}}
 */
export function decompose(value: Value): Decomposed {
  let matrix = fromObject(value);

  return {
    scaleFactor: matrix.a,
    translationX: matrix.e,
    translationY: matrix.f,
  };
}

/**
 *
 * @param value
 * @param focus
 * @returns {Object}
 */
export function setFocus(value: Value, focus: any): Value {
  return set(value, { focus });
}

/**
 *
 * @param value
 * @param viewerWidth
 * @param viewerHeight
 * @returns {Object}
 */
export function setViewerSize(value: Value, viewerWidth: number, viewerHeight: number) {
  return set(value, { viewerWidth, viewerHeight });
}

/**
 *
 * @param value
 * @param SVGMinX
 * @param SVGMinY
 * @param SVGWidth
 * @param SVGHeight
 * @returns {Object}
 */
export function setSVGViewBox(
  value: Value,
  SVGMinX: number,
  SVGMinY: number,
  SVGWidth: number,
  SVGHeight: number
) {
  return set(value, { SVGMinX, SVGMinY, SVGWidth, SVGHeight });
}

/**
 *
 * @param value
 * @param scaleFactorMin
 * @param scaleFactorMax
 * @returns {Object}
 */
//TODO rename to setZoomLimits
export function setZoomLevels(value: Value, scaleFactorMin: number, scaleFactorMax: number) {
  return set(value, { scaleFactorMin, scaleFactorMax });
}

/**
 *
 * @param value
 * @returns {Object}
 */
export function resetMode(value: Value) {
  return set(value, {
    mode: DEFAULT_MODE,
    startX: null,
    startY: null,
    endX: null,
    endY: null,
  });
}

export default function parseViewBox(viewBoxString: string) {
  // viewBox specs: https://www.w3.org/TR/SVG/coords.html#ViewBoxAttribute
  return viewBoxString
    .split(/[ ,]/) // split optional comma
    .filter(Boolean) // remove empty strings
    .map(Number); // cast to Number
}

export function startZooming(value: Value, viewerX: number, viewerY: number) {
  return set(value, {
    mode: MODE_ZOOMING,
    startX: viewerX,
    startY: viewerY,
    endX: viewerX,
    endY: viewerY,
  });
}

export function updateZooming(value: Value, viewerX: number, viewerY: number) {
  if (value.mode !== MODE_ZOOMING)
    throw new Error('update selection not allowed in this mode ' + value.mode);

  return set(value, {
    endX: viewerX,
    endY: viewerY,
  });
}

export function stopZooming(
  value: Value,
  viewerX: number,
  viewerY: number,
  scaleFactor: number
): Value {
  const TOLERATED_DISTANCE = 7; //minimum distance to choose if area selection or drill down on point
  let { startX, startY } = value;

  if (!startX || !startY) return value;

  let start = getSVGPoint(value, startX, startY);
  let end = getSVGPoint(value, viewerX, viewerY);

  if (
    Math.abs(startX - viewerX) > TOLERATED_DISTANCE &&
    Math.abs(startY - viewerY) > TOLERATED_DISTANCE
  ) {
    let box = calculateBox(start, end);
    if (!box) return value;
    return fitSelection(value, box.x, box.y, box.width, box.height);
  } else {
    let SVGPoint = getSVGPoint(value, viewerX, viewerY);
    return zoom(value, SVGPoint.x, SVGPoint.y, scaleFactor);
  }
}

interface Box {
  x: number;
  y: number;
  width: number;
  height: number;
}

export function calculateBox(start: Point, end: Point): Box | undefined {
  if (start.x <= end.x && start.y <= end.y) {
    return {
      x: start.x,
      y: start.y,
      width: end.x - start.x,
      height: end.y - start.y,
    };
  } else if (start.x >= end.x && start.y <= end.y) {
    return {
      x: end.x,
      y: start.y,
      width: start.x - end.x,
      height: end.y - start.y,
    };
  } else if (start.x >= end.x && start.y >= end.y) {
    return {
      x: end.x,
      y: end.y,
      width: start.x - end.x,
      height: start.y - end.y,
    };
  } else if (start.x <= end.x && start.y >= end.y) {
    return {
      x: start.x,
      y: end.y,
      width: end.x - start.x,
      height: start.y - end.y,
    };
  }
}
