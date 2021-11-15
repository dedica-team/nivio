import {
  IAssessment,
  IChanges,
  IGroup,
  IItem,
  ILandscape,
  INotificationMessage,
} from '../../interfaces';
import { LandscapeContextType } from '../../Context/LandscapeContext';

const items: IItem[] = [
  {
    contact: 'marvin',
    owner: 'daniel',
    group: 'groupA',
    fullyQualifiedIdentifier: 'test/groupA/foo',
    identifier: 'foo',
    name: 'A foo item',
    description: 'testDescription',
    relations: {},
    labels: {},
    tags: [],
    type: 'service',
    icon: '',
    networks: [],
  },
];

const groups: IGroup[] = [
  {
    fullyQualifiedIdentifier: 'test/groupA',
    name: 'A Group',
    items: items,
    identifier: 'groupA',
  },
];

const landscape: ILandscape = {
  name: 'landscapeTestName',
  identifier: 'test',
  description: 'testIdentifier',
  groups: groups,
  lastUpdate: 'gestern',
  contact: 'marvin',
  owner: 'daniel',
  fullyQualifiedIdentifier: 'fullTestIdentifier',
};
const assessments: IAssessment = {
  date: '',
  results: {
    'test/groupA/foo': [
      {
        identifier: 'test/groupA/foo',
        field: 'foo',
        status: 'yellow',
        message: 'bar',
        summary: true,
      },
    ],
    'test/groupA': [
      {
        identifier: 'test/groupA',
        field: 'foo',
        status: 'yellow',
        message: 'bar',
        summary: true,
      },
    ],
    'test': [
      {
        identifier: 'test',
        field: 'overall',
        status: 'yellow',
        message: 'The landscape is somehow broken.',
        summary: true,
      },
    ],
  },
};

const changes: INotificationMessage = {
  timestamp: '',
  date: new Date(),
  landscape: 'test',
  level: 'info',
  type: 'ProcessingFinishedEvent',
  changelog: { changes: {} },
};

const landscapeContextValue: LandscapeContextType = {
  identifier: 'test',
  landscape: landscape,
  assessment: assessments,
  changes: changes,
  next: typeof jest != 'undefined' ? jest.fn() : () => {},
  getAssessmentSummary: (fqi) => {
    return assessments.results[fqi].find((assessmentResult) => assessmentResult.summary) || null;
  },
  notification: null,
};

export default landscapeContextValue;
