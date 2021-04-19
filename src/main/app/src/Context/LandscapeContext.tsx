import React, { useEffect, useState } from 'react';
import { IAssessment, IAssessmentProps, ILandscape } from '../interfaces';
import { get } from '../utils/API/APIClient';

export interface LandscapeContextType {
  readonly landscape: ILandscape | null;
  readonly assessment: IAssessment | null;
  readonly identifier: string | null;
  next: (identifier: string | null) => void;
  getAssessmentSummary: (fqi: string) => IAssessmentProps | null;
}

export const LandscapeContext = React.createContext<LandscapeContextType>({
  landscape: null,
  assessment: null,
  identifier: null,
  next: () => {},
  getAssessmentSummary: () => {
    return null;
  },
});

/**
 * Provides the current landscape and assessment.
 *
 * Responsible for loading objects from the API.
 *
 * @param props
 * @constructor
 */
const LandscapeContextProvider: React.FC<{}> = (props) => {
  const [landscape, setLandscape] = useState<ILandscape | null>(null);
  const [assessment, setAssessment] = useState<IAssessment | null>(null);
  const [identifier, setIdentifier] = useState<string | null>(null);

  //load landscape
  useEffect(() => {
    if (identifier == null) {
      console.debug(`Identifier not present`);
      return;
    }

    get(`/api/${identifier}`).then((response) => {
      setLandscape(response);
      console.debug(`Loaded landscape data for ${identifier}`);
    });
    get(`/assessment/${identifier}`).then((response) => {
      setAssessment(response);
      console.debug(`Loaded assessment data for ${identifier}`);
    });
  }, [identifier]);

  return (
    <LandscapeContext.Provider
      value={{
        landscape: landscape,
        assessment: assessment,
        identifier: identifier,
        next: (nextId) => {
          if (identifier === nextId) return;
          console.debug('New identifier', nextId);
          setIdentifier(nextId);
        },

        getAssessmentSummary: (fqi) => {
          if (!assessment) return null;
          const assessmentResults = assessment.results[fqi];
          if (!assessmentResults) return null;

          return assessmentResults.find((assessmentResult) => assessmentResult.summary) || null;
        },
      }}
    >
      {props.children}
    </LandscapeContext.Provider>
  );
};

export { LandscapeContextProvider };
