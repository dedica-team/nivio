import React from 'react';
import { Box } from '@material-ui/core';

export interface TabPanelProps {
  children?: React.ReactNode;
  index: number;
  value: number;
  prefix: string;
}

export const a11yProps = (index: number, prefix: string) => {
  return {
    'id': `${prefix}-tab-${index}`,
    'aria-controls': `${prefix}-tabpanel-${index}`,
  };
};

export const TabPanel = function (props: TabPanelProps) {
  const { children, value, index, prefix, ...other } = props;

  return (
    <div
      role='tabpanel'
      hidden={value !== index}
      id={`${prefix}-tabpanel-${index}`}
      aria-labelledby={`${prefix}-tab-${index}`}
      {...other}
    >
      {value === index && <Box>{children}</Box>}
    </div>
  );
};