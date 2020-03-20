import React from 'react';

interface ICommandContext{
    message: string,
}

const CommandContextDefaultValues = {
    message: "",
};

const CommandContext = React.createContext<ICommandContext>(CommandContextDefaultValues);

export default CommandContext;