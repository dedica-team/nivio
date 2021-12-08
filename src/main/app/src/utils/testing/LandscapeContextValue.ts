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
    icon: '',
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
const changes: IChanges = {
  test: {
    changeType: '',
    componentType: '',
    message: '',
  },
};
const notification: INotificationMessage = {
  timestamp: 'test',
  landscape: 'test',
  message: 'test',
  level: 'success',
  type: 'test',
  date: new Date(),
  changelog: { changes },
};

const landscapeContextValue: LandscapeContextType = {
  identifier: 'test',
  landscape: landscape,
  assessment: assessments,
  changes: notification,
  next: typeof jest != 'undefined' ? jest.fn() : () => {},
  getAssessmentSummary: (fqi) => {
    if (!assessments.results[fqi]) {
      return null;
    }
    return assessments.results[fqi].find((assessmentResult) => assessmentResult.summary) || null;
  },
  notification: null,
};

export default landscapeContextValue;
