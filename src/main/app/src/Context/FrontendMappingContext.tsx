import React, {useEffect, useState} from 'react';
import {get} from "../utils/API/APIClient";


export interface FrontendMappingContextType {
    frontendMapping: Map<string, string>;
}

export const FrontendMappingContext = React.createContext<FrontendMappingContextType>({
    frontendMapping: new Map<string, string>()
});

const FrontendMappingProvider: React.FC = ({children}) => {
    const [frontendMapping, setFrontendMapping] = useState(new Map());

    useEffect(() => {
        get(`/api/mapping`).then((response) => {
            setFrontendMapping(new Map(Object.entries(response)));
        });
    })

    return (<FrontendMappingContext.Provider value={{
        frontendMapping: frontendMapping
    }}>
        {children}
    </FrontendMappingContext.Provider>);
}
export {FrontendMappingProvider};