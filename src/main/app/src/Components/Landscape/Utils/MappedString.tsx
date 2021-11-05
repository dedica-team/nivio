import React, { useContext } from 'react';
import { FrontendMappingContext } from '../../../Context/FrontendMappingContext';

interface Props {
  mapKey: string;
}

const MappedString: React.FC<Props> = ({ mapKey }) => {
  const map = useContext(FrontendMappingContext);
  if (map.frontendMapping.has(mapKey)) {
    return <>{map.frontendMapping.get(mapKey)}</>;
  } else {
    return <>{mapKey}</>;
  }
};
export default MappedString;
