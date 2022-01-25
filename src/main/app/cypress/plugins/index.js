// export a function
module.exports = (on, config) => {
  // bind to the event we care about
  on('<event>', (arg1, arg2) => {
    // plugin stuff here
  });
}