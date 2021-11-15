import React from 'react';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { Box, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    box: {
      padding: 2,
      borderRadius: 5,
      backgroundColor: theme.palette.secondary.dark,
      display: 'flex',
      justifyContent: 'center',
    },
    green: {
      backgroundColor: 'green',
      width: 15,
      height: 15,
      marginRight: 1,
      borderRadius: 50,
    },
    yellow: {
      backgroundColor: 'yellow',
      width: 15,
      height: 15,
      marginRight: 1,
      borderRadius: 50,
    },
    orange: {
      backgroundColor: 'orange',
      width: 15,
      height: 15,
      marginRight: 1,
      borderRadius: 50,
    },
    red: {
      backgroundColor: 'red',
      width: 15,
      height: 15,
      marginRight: 1,
      borderRadius: 50,
    },
    brown: {
      backgroundColor: 'brown',
      width: 15,
      height: 15,
      borderRadius: 50,
    },
    none: {
      backgroundColor: '#666',
      width: 15,
      height: 15,
      marginRight: 1,
      borderRadius: 50,
    },
  })
);

interface Props {
  name?: string;
  value?: string;
  status: string;
  style?: any;
}

const StatusChip: React.FC<Props> = ({ name, value, status }) => {
  const classes: Record<string, any> = useStyles();
  let level: number = 0;
  let usedClass;
  if (status.toLowerCase() === 'green') {
    level = 1;
    usedClass = classes.green;
  }
  if (status.toLowerCase() === 'yellow') {
    level = 2;
    usedClass = classes.yellow;
  }
  if (status.toLowerCase() === 'orange') {
    level = 3;
    usedClass = classes.orange;
  }
  if (status.toLowerCase() === 'red') {
    level = 4;
    usedClass = classes.red;
  }
  if (status.toLowerCase() === 'brown') {
    level = 5;
    usedClass = classes.brown;
  }

  let label = name || '';
  if (value != null && value !== 'null') {
    if (label.length > 0) label += ': ';
    label += value;
  }
  return (
    <Box className={classes.box} title={label}>
      <Box className={level > 0 ? usedClass : classes.none} />
      <Box className={level > 1 ? usedClass : classes.none} />
      <Box className={level > 2 ? usedClass : classes.none} />
      <Box className={level > 3 ? usedClass : classes.none} />
      <Box className={level > 4 ? usedClass : classes.none} />
    </Box>
  );
};

export default StatusChip;
