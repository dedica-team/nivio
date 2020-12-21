import React from 'react';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    chip: {
      marginBottom: 5,
      marginRight: 5,
      maxWidth: '100%',
      textShadow: 'none',
      fontFamily: 'monospace',
      backgroundColor: theme.palette.secondary.dark,
      color: 'white',
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
  })
);

interface Props {
  name: string;
  value?: string;
  status: string;
}

const StatusChip: React.FC<Props> = ({ name, value, status }) => {
  const classes: Record<string, any> = useStyles();
  let letter = '?';
  if (status.toLowerCase() === 'green') letter = '✓';
  if (status.toLowerCase() === 'yellow') letter = '';
  if (status.toLowerCase() === 'orange') letter = '*';
  if (status.toLowerCase() === 'red') letter = '!';
  if (status.toLowerCase() === 'brown') letter = '!!';

  let label = name;
  if (value) {
    label += ': ' + value;
  }
  return (
    <Chip
      size={'small'}
      className={classes.chip}
      avatar={<Avatar className={classes[status.toLowerCase()]}>{letter}</Avatar>}
      label={label}
    />
  );
};

export default StatusChip;
