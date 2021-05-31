//http://stackoverflow.com/a/4819886/1398836

export default function detectTouch() {
  return 'ontouchstart' in window        // works on most browsers
    || navigator.maxTouchPoints;       // works on IE10/11 and Surface
};
