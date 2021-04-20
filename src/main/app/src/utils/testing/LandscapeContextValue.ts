import { IAssessment, IGroup, IItem, ILandscape } from "../../interfaces";
import { LandscapeContextType } from "../../Context/LandscapeContext";

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
        field: 'foo',
        status: 'yellow',
        message: 'bar',
        summary: true,
      },
    ],
    'test/groupA': [
      {
        field: 'foo',
        status: 'yellow',
        message: 'bar',
        maxField: 'foo',
        summary: true,
      },
    ],
    'test': [
      {
        field: 'overall',
        status: 'yellow',
        message: 'The landscape is somehow broken.',
        maxField: 'foo',
        summary: true,
      },
    ],
  },
};

const landscapeContextValue : LandscapeContextType = {
  identifier: 'test',
  landscape: landscape,
  assessment: assessments,
  next: jest.fn(),
  getAssessmentSummary: (fqi) => {
    return assessments.results[fqi].find((assessmentResult) => assessmentResult.summary) || null;
  },
  notification: null,
}

export default landscapeContextValue;