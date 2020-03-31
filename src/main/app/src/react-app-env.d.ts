/// <reference types="react-scripts" />
declare module 'react-console-emulator';
declare module 'react-svg-pan-zoom-loader';
declare module 'raw.macro' {
    const loader: (path: string) => string;
    export default loader;
}