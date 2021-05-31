import * as React from "react";
import { ViewerMouseEvent } from "./events";
import {
  ALIGN_BOTTOM,
  ALIGN_CENTER,
  ALIGN_LEFT,
  ALIGN_RIGHT,
  ALIGN_TOP, POSITION_LEFT,
  POSITION_NONE, POSITION_RIGHT, Tool,
  ToolbarPosition, Value
} from "./ReactSVGPanZoom";

export interface OptionalProps {
  // background of the viewer
  background: string;

  // background of the svg
  SVGBackground: string;

  // CSS style of the Viewer
  style: object;

  // className of the Viewer
  className: string;

  // detect zoom operation performed trough pinch gesture or mouse scroll
  detectWheel: boolean;

  // perform PAN if the mouse is on viewer border
  detectAutoPan: boolean;

  detectPinchGesture: boolean;

  toolbarProps: {
    position?: ToolbarPosition;
    SVGAlignX?: typeof ALIGN_CENTER | typeof ALIGN_LEFT | typeof ALIGN_RIGHT;
    SVGAlignY?: typeof ALIGN_CENTER | typeof ALIGN_TOP | typeof ALIGN_BOTTOM;
  };

  customMiniature: React.ReactElement | React.ComponentType;
  miniatureProps: {
    position: typeof POSITION_NONE | typeof POSITION_RIGHT | typeof POSITION_LEFT;
    background: string;
    width: number;
    height: number;
  };

  // Note: The `T` type parameter is the type of the `target` of the event:
  // handler click
  onClick<T>(event: ViewerMouseEvent<T>): void;

  // handler double click
  onDoubleClick<T>(event: ViewerMouseEvent<T>): void;

  // handler mouseup
  onMouseUp<T>(event: ViewerMouseEvent<T>): void;

  // handler mousemove
  onMouseMove<T>(event: ViewerMouseEvent<T>): void;

  // handler mousedown
  onMouseDown<T>(event: ViewerMouseEvent<T>): void;

  // handler zoom level changed
  onZoom<T>(event: ViewerMouseEvent<T>): void;

  // handler pan action performed
  onPan<T>(event: ViewerMouseEvent<T>): void;

  // if disabled the user can move the image outside the viewer
  preventPanOutside: boolean;

  // how much scale in or out
  scaleFactor: number;

  // how much scale in or out on mouse wheel (requires detectWheel enabled)
  scaleFactorOnWheel: number;

  // maximum amount of scale a user can zoom in to
  scaleFactorMax: number;

  // minimum amount of a scale a user can zoom out of
  scaleFactorMin: number;

  // modifier keys //https://developer.mozilla.org/en-US/docs/Web/API/KeyboardEvent/getModifierState
  modifierKeys: string[];

  // Turn off zoom on double click
  disableDoubleClickZoomWithToolAuto: boolean;

  // override default toolbar component
  // TODO: specify function type more clearly
  customToolbar: React.Component<any> | React.StatelessComponent<any>;

  // How about touch events? They are in README but not in `propTypes`.
}

export interface RequiredProps {
  // width of the viewer displayed on screen
  width: number;
  // height of the viewer displayed on screen
  height: number;
  // current active tool (TOOL_NONE, TOOL_PAN, TOOL_ZOOM_IN, TOOL_ZOOM_OUT)
  tool: Tool;
  // value of the viewer (current point of view)
  value: Value | null;

  // handler tool changed
  onChangeTool(tool: Tool): void;

  // handler something changed
  onChangeValue(value: Value): void;

  // accept only one node SVG
  // TODO: Figure out how to constrain `children` or maybe just leave it commented out
  // because `children` is already implicit props
  // children: () => any;
}

export interface UncontrolledExtraOptionalProps {
  // current active tool (TOOL_NONE, TOOL_PAN, TOOL_ZOOM_IN, TOOL_ZOOM_OUT)
  tool: Tool;
  // value of the viewer (current point of view)
  value: Value | null;

  // handler tool changed
  onChangeTool(tool: Tool): void;

  // handler something changed
  onChangeValue(value: Value): void;
}

export interface UncontrolledRequiredProps {
  // width of the viewer displayed on screen
  width: number;
  // height of the viewer displayed on screen
  height: number;
}

export type Props = RequiredProps & Partial<OptionalProps>;

export type UncontrolledProps = UncontrolledRequiredProps &
  Partial<OptionalProps> &
  Partial<UncontrolledExtraOptionalProps>;