import React from 'react';
import { Value } from 'react-svg-pan-zoom';

const drawerWidth = 320;
it('should not change values', () => {
  // @ts-ignore
  const value: Value = {
    viewerWidth: 1000,
    viewerHeight: 1000,
    SVGWidth: 2000,
    SVGHeight: 2000,
  };
  const centerCoordinates = getApproximateCenterCoordinates(value, '1000', '1000');
  expect(centerCoordinates.x).toBe(1000);
  expect(centerCoordinates.y).toBe(1000);
});

it('should change lower x', () => {
  // @ts-ignore
  const value: Value = {
    viewerWidth: 1000,
    viewerHeight: 1000,
    SVGWidth: 2000,
    SVGHeight: 2000,
  };
  const centerCoordinates = MapUtils.getApproximateCenterCoordinates(value, '100', '1000');
  expect(centerCoordinates.x).toBe(500 - drawerWidth);
  expect(centerCoordinates.y).toBe(1000);
});

it('should change upper x', () => {
  // @ts-ignore
  const value: Value = {
    viewerWidth: 1000,
    viewerHeight: 1000,
    SVGWidth: 2000,
    SVGHeight: 2000,
  };
  const centerCoordinates = MapUtils.getApproximateCenterCoordinates(value, '1999', '1000');
  expect(centerCoordinates.x).toBe(1500 + drawerWidth);
  expect(centerCoordinates.y).toBe(1000);
});

it('should change lower y', () => {
  // @ts-ignore
  const value: Value = {
    viewerWidth: 1000,
    viewerHeight: 1000,
    SVGWidth: 2000,
    SVGHeight: 2000,
  };
  const centerCoordinates = MapUtils.getApproximateCenterCoordinates(value, '1000', '100');
  expect(centerCoordinates.x).toBe(1000);
  expect(centerCoordinates.y).toBe(500);
});

it('should change upper y', () => {
  // @ts-ignore
  const value: Value = {
    viewerWidth: 1000,
    viewerHeight: 1000,
    SVGWidth: 2000,
    SVGHeight: 2000,
  };
  const centerCoordinates = MapUtils.getApproximateCenterCoordinates(value, '1000', '1999');
  expect(centerCoordinates.x).toBe(1000);
  expect(centerCoordinates.y).toBe(1500);
});
