import { fromObject, translate, transform, applyToPoints } from 'transformation-matrix';
import { ACTION_PAN, getSVGPoint, MODE_IDLE, MODE_PANNING, set, Value } from './ReactSVGPanZoom';

/**
 * Atomic pan operation
 * @param value
 * @param SVGDeltaX drag movement
 * @param SVGDeltaY drag movement
 * @param panLimit forces the image to keep at least x pixel inside the viewer
 * @returns {Object}
 */
export function pan(
  value: Value,
  SVGDeltaX: number,
  SVGDeltaY: number,
  panLimit: number | undefined = undefined
) {
  let matrix = transform(
    fromObject(value), //2
    translate(SVGDeltaX, SVGDeltaY) //1
  );

  // apply pan limits
  if (panLimit) {
    let [{ x: x1, y: y1 }, { x: x2, y: y2 }] = applyToPoints(matrix, [
      { x: value.SVGMinX + panLimit, y: value.SVGMinY + panLimit },
      {
        x: value.SVGMinX + value.SVGWidth - panLimit,
        y: value.SVGMinY + value.SVGHeight - panLimit,
      },
    ]);

    //x limit
    let moveX = 0;
    if (value.viewerWidth - x1 < 0) moveX = value.viewerWidth - x1;
    else if (x2 < 0) moveX = -x2;

    //y limit
    let moveY = 0;
    if (value.viewerHeight - y1 < 0) moveY = value.viewerHeight - y1;
    else if (y2 < 0) moveY = -y2;

    //apply limits
    matrix = transform(translate(moveX, moveY), matrix);
  }

  return set(
    value,
    {
      mode: MODE_IDLE,
      ...matrix,
    },
    ACTION_PAN
  );
}

/**
 * Start pan operation lifecycle
 * @param value
 * @param viewerX
 * @param viewerY
 * @return {ReadonlyArray<unknown>}
 */
export function startPanning(value: Value, viewerX: number, viewerY: number) {
  return set(
    value,
    {
      mode: MODE_PANNING,
      startX: viewerX,
      startY: viewerY,
      endX: viewerX,
      endY: viewerY,
    },
    ACTION_PAN
  );
}

/**
 * Continue pan operation lifecycle
 * @param value
 * @param viewerX
 * @param viewerY
 * @param panLimit
 * @return {ReadonlyArray<unknown>}
 */
export function updatePanning(
  value: Value,
  viewerX: number,
  viewerY: number,
  panLimit: number
): Value {
  if (value.mode !== MODE_PANNING)
    throw new Error('update pan not allowed in this mode ' + value.mode);

  let { endX, endY } = value;

  if (!endX || !endY) return value;
  let start = getSVGPoint(value, endX, endY);
  let end = getSVGPoint(value, viewerX, viewerY);

  let deltaX = end.x - start.x;
  let deltaY = end.y - start.y;

  let nextValue = pan(value, deltaX, deltaY, panLimit);
  return set(
    nextValue,
    {
      mode: MODE_PANNING,
      endX: viewerX,
      endY: viewerY,
    },
    ACTION_PAN
  );
}

/**
 * Stop pan operation lifecycle
 * @param value
 * @return {ReadonlyArray<unknown>}
 */
export function stopPanning(value: Value) {
  return set(
    value,
    {
      mode: MODE_IDLE,
      startX: null,
      startY: null,
      endX: null,
      endY: null,
    },
    ACTION_PAN
  );
}

/**
 * when pointer is on viewer edge -> pan image
 * @param value
 * @param viewerX
 * @param viewerY
 * @return {ReadonlyArray<any>}
 */
export function autoPanIfNeeded(value: Value, viewerX: number, viewerY: number) {
  let deltaX = 0;
  let deltaY = 0;

  if (viewerY <= 20) deltaY = 2;
  if (value.viewerWidth - viewerX <= 20) deltaX = -2;
  if (value.viewerHeight - viewerY <= 20) deltaY = -2;
  if (viewerX <= 20) deltaX = 2;

  deltaX = deltaX / value.d;
  deltaY = deltaY / value.d;

  return deltaX === 0 && deltaY === 0 ? value : pan(value, deltaX, deltaY);
}
