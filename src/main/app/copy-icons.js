#!/usr/bin/env node
const fs = require('fs-extra');

// Async with promises:
fs.copy('node_modules/@mdi/svg/svg', '../resources/static/icons/svg')
  .then(() => console.log('Icons copied successfully!'))
  .catch((err) => console.error(err));
