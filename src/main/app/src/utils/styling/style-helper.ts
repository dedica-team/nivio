import { IAssessmentProps } from '../../interfaces';

export const getAssessmentColorAndMessage = (
  assessmentResults: IAssessmentProps[] | null,
  itemIdentifier: string
): string[] => {
  let assessmentColor = 'grey';
  let assessmentMessage = '';

  if (assessmentResults) {
    const result = assessmentResults.find(
      (assessmentResult) => assessmentResult.field === `summary.${itemIdentifier}`
    );

    if (result) {
      if (result.status !== 'UNKNOWN') {
        assessmentColor = result.status;
        assessmentMessage = result.message;
      } else {
        assessmentMessage = 'unknown status';
      }
    }
  }

  return [assessmentColor, assessmentMessage];
};
