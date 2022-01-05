import React, { useContext } from 'react';
import { FrontendMappingContext, FrontendMappingContextType } from "../../../Context/FrontendMappingContext";

interface Props {
  mapKey: string;
}

/**
 * Returns a term to display in place of the given key.
 *
 * @param mapKey an internally used key
 * @returns the mapped/translated value or the key if mapping is absent
 * @constructor
 */
const MappedString: React.FC<Props> = ({ mapKey }) => {
  const contextType: FrontendMappingContextType = useContext(FrontendMappingContext);
  if (contextType.frontendMapping.keys.has(mapKey)) {
    return <>{contextType.frontendMapping.keys.get(mapKey)}</>;
  } else {
    return <>{mapKey}</>;
  }
};
export default MappedString;
