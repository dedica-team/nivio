import React, { useEffect, useState } from 'react';
import { get } from '../utils/API/APIClient';

interface FrontendMapping {
  keys: Map<string, string>;
  descriptions: Map<string, string>;
}

export interface FrontendMappingContextType {
  frontendMapping: FrontendMapping;
}

export const FrontendMappingContext = React.createContext<FrontendMappingContextType>({
  frontendMapping: {
    keys: new Map([]),
    descriptions: new Map([]),
  },
});

const FrontendMappingProvider: React.FC = ({ children }) => {
  const [frontendMapping, setFrontendMapping] = useState<FrontendMapping>({
    keys: new Map([]),
    descriptions: new Map([]),
  });

  useEffect(() => {
    get(`/api/mapping`).then((response) => {
      // @ts-ignore
      const keys: Map<string, string> = new Map(Object.entries(response.keys));
      // @ts-ignore
      const descriptions: Map<string, string> = new Map(Object.entries(response.descriptions));
      setFrontendMapping({ keys: keys, descriptions: descriptions });
    });
  }, []);

  return (
    <FrontendMappingContext.Provider
      value={{
        frontendMapping: frontendMapping,
      }}
    >
      {children}
    </FrontendMappingContext.Provider>
  );
};
export { FrontendMappingProvider };
