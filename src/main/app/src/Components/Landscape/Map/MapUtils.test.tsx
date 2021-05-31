import React from 'react';
import { getApproximateCenterCoordinates } from "./MapUtils";

it('should not change values because viewbox not negative', () => {
  // @ts-ignore
  const value: DOMRect = {
    x: 0,
    y: 0,
    width: 2000,
    height: 2000,
  };
  const centerCoordinates = getApproximateCenterCoordinates(value, 1000, 1000, 200, 200);
  expect(centerCoordinates.x).toBe(200);
  expect(centerCoordinates.y).toBe(200);
});

it('should change lower x', () => {
  // @ts-ignore
  const value: DOMRect = {
    x: -200,
    y: 0,
    width: 2000,
    height: 2000,
  };
  const centerCoordinates = getApproximateCenterCoordinates(value, 1000, 1000, 200, 200);
  expect(centerCoordinates.x).toBe(300);
  expect(centerCoordinates.y).toBe(200);
});

it('should change upper x', () => {
  // @ts-ignore
  const value: DOMRect = {
    x: -200,
    y: 0,
    width: 2000,
    height: 2000,
  };
  const centerCoordinates = getApproximateCenterCoordinates(value, 1000, 1000, 1500, 200);
  expect(centerCoordinates.x).toBe(1450);
  expect(centerCoordinates.y).toBe(200);
});
