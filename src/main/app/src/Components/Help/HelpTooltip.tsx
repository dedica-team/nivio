import { Theme, Tooltip, withStyles } from '@material-ui/core';
import React, { ReactElement } from 'react';
import { HelpOutline } from '@material-ui/icons';

interface HelpProps {
  content: ReactElement;
  style?: React.CSSProperties | undefined;
}

const HtmlTooltip = withStyles((theme: Theme) => ({
  tooltip: {
    backgroundColor: theme.palette.primary.main,
    fontSize: theme.typography.pxToRem(14),
    boxShadow: '2px 2px 5px black'
  },
}))(Tooltip);

/**
 * A standardized help icon that works as tooltip.
 *
 * @param content the help content
 * @param style additional css stylings
 * @constructor
 */
const HelpTooltip: React.FC<HelpProps> = ({ content, style }) => {
  return <HtmlTooltip title={content} children={<HelpOutline />} style={style} aria-label={'help'}/>;
};

export default HelpTooltip;