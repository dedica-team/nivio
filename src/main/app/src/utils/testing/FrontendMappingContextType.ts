import { FrontendMappingContextType } from '../../Context/FrontendMappingContext';

const frontendMappingContextType: FrontendMappingContextType = {
  frontendMapping: new Map<string, string>([
    ['shortname', 'short name'],
    ['END_OF_LIFE', 'end of life'],
  ]),
};
export default frontendMappingContextType;
