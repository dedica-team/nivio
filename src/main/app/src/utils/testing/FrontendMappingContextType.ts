import { FrontendMappingContextType } from '../../Context/FrontendMappingContext';

const frontendMappingContextType: FrontendMappingContextType = {
  frontendMapping: {
    keys: new Map<string, string>([
      ['shortname', 'short name'],
      ['END_OF_LIFE', 'end of life'],
    ]),
    descriptions: new Map<string, string>([
      ['END_OF_LIFE', 'Whether an item is planned, outdated etc.'],
    ]),
  },
};
export default frontendMappingContextType;
