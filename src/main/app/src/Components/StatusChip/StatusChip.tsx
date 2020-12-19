import React from 'react';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import { Theme } from '@material-ui/core';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    green: {
      backgroundColor: 'green',
    },
    yellow: {
      backgroundColor: 'yellow',
    },
    red: {
      backgroundColor: 'red',
    },
  })
);

interface Props {
  name: string;
  value: string;
  status: string;
}

const StatusChip: React.FC<Props> = ({ name, value, status }) => {
  const classes: Record<string, any> = useStyles();
  return (
    <Chip
      avatar={<Avatar className={classes[status.toLowerCase()]} ></Avatar>}
      label={name + ': ' + value}
    />
  );
};

export default StatusChip;
