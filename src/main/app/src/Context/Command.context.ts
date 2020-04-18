import React from 'react';

/**
 * CommandContext is used to display the a consistent message on every site of nivio where our Command Component is used
 */
interface ICommandContext {
  message: string;
}

const CommandContextDefaultValues = {
  message: '',
};

const CommandContext = React.createContext<ICommandContext>(CommandContextDefaultValues);

export default CommandContext;
