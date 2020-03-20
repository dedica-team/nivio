import React from 'react';

const LandscapeContextDefaultValues = {
    landscapes: null,
    selectedLandscape: null,
};

const LandscapeContext = React.createContext(LandscapeContextDefaultValues);

export default LandscapeContext;