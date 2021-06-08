import React from 'react';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { Box, Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    chip: {
      marginBottom: 5,
      marginRight: 5,
      maxWidth: '100%',
      fontFamily: 'monospace',
      backgroundColor: theme.palette.primary.main,
      color: 'white',
    },
    box: {
      marginRight: 5,
      padding: 2,
      borderRadius: 5,
      textAlign: 'center',
      width: '4rem',
      maxWidth: '100%',
      fontFamily: 'monospace',
      backgroundColor: theme.palette.primary.main,
      color: 'white',
      fontSize: 'x-small'
    },
    green: {
      backgroundColor: 'green',
    },
    yellow: {
      backgroundColor: 'yellow',
    },
    orange: {
      backgroundColor: 'orange',
    },
    red: {
      backgroundColor: 'red',
    },
    brown: {
      backgroundColor: 'brown',
    },
  })
);

interface Props {
  name?: string;
  value?: string;
  status: string;
  style?: any;
}

const StatusChip: React.FC<Props> = ({ name, value, status}) => {
  const classes: Record<string, any> = useStyles();
  let letter = '?';
  if (status.toLowerCase() === 'green') letter = '✓';
  if (status.toLowerCase() === 'yellow') letter = '*';
  if (status.toLowerCase() === 'orange') letter = '!';
  if (status.toLowerCase() === 'red') letter = '⚠';
  if (status.toLowerCase() === 'brown') letter = '☠';

  let label = name || '';
  if (value != null && value !== 'null') {
    if (label.length > 0)
    label += ': ';
    label += value;
  }
  return (
    <Box style={{float: 'left'}} className={classes.box}>
      <Avatar className={classes[status.toLowerCase()]} style={{margin: 'auto'}}>{letter}</Avatar>
      {label}
    </Box>
  );
};

export default StatusChip;
