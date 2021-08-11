#!/usr/bin/env node
const fs = require('fs-extra');

fs.copy('node_modules/@mdi/svg/svg', '../resources/static/icons/svg')
  .then(() => console.log('Icons copied successfully!'))
  .catch((err) => console.error(err));

fs.copy('node_modules/@mdi/svg/meta.json', '../resources/static/icons/meta.json')
  .then(() => console.log('Icon meta.json list copied successfully!'))
  .catch((err) => console.error(err));
