import React, { useState, useEffect } from 'react';
import { withTheme } from '@rjsf/core';
import { Theme as MaterialUITheme } from '@rjsf/material-ui';
import './Editor.scss';

const Form = withTheme(MaterialUITheme);
const schema = require('./nivio_schema.json');

const NivioEditor = () => {
  const [yourJson, setYourJson] = useState<any | null>({ identifier: null });

  const onSubmit = (val: any) => {
    setYourJson(val);
    console.log(val);
  };

  //https://github.com/vankop/jsoneditor-react/blob/master/src/Editor.jsx
  return (
    <div className={'editor'}>
      <Form schema={schema} onSubmit={(val) => onSubmit(val)} />
    </div>
  );
};

export default NivioEditor;
