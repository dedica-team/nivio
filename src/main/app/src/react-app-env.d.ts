/// <reference types="react-scripts" />
declare module 'react-console-emulator';
declare module 'react-svg-pan-zoom-loader';

/**
 * Type definition for the raw loader.
 *
 * Required when importing the loader from a TypeScript file:
 *
 *     import raw from "raw.macro";
 *
 * When using pure JavaScript, IntelliJ uses this definition to provide
 * type hints (parameters etc.).
 */
declare module 'raw.macro' {
  /**
   * Loads file contents at compile time:
   *
   *     const text = raw("./example.txt");
   *
   * E.g. useful to load templates directly into components.
   */
  const loader: (path: string) => string;
  export default loader;
}
