import React from 'react';
import './LevelChip.scss';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';

interface LevelChipProps {
  title: string;
  level: string;
}

const LevelChip: React.FC<LevelChipProps> = ({ title, level }) => {
  return (
    <Chip
      className={'levelChip'}
      size={'small'}
      avatar={<Avatar className={level.toLowerCase()}>{level.substr(0, 1).toUpperCase()}</Avatar>}
      label={title}
    />
  );
};

export default LevelChip;
