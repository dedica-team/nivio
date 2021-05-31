import * as React from "react";
import { Point } from "./ReactSVGPanZoom";

export interface ViewerMouseEvent<T> {
  originalEvent: React.MouseEvent<T>;
  SVGViewer: SVGSVGElement;
  point: Point;
  x: number;
  y: number;
  scaleFactor: number;
  translationX: number;
  translationY: number;

  preventDefault(): void;

  stopPropagation(): void;
}

export interface ViewerTouchEvent<T> {
  originalEvent: React.TouchEvent<T>;
  SVGViewer: SVGSVGElement;
  points: Point[];
  changedPoints: Point[];
  scaleFactor: number;
  translationX: number;
  translationY: number;

  preventDefault(): void;

  stopPropagation(): void;
}