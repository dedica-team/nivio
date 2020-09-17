import React, { useEffect, useState, ReactElement } from 'react';
import { get } from '../../../utils/API/APIClient';
import './LandscapeAssessment.scss';

import { IAssessmentProps, IAssessmentResults, IGroup, IItem } from '../../../interfaces';
import {
  getAssessmentSummaryColorAndMessage,
  getAssessmentColor,
} from '../../../utils/styling/style-helper';

interface Props {
  fullyQualifiedIdentifier: string;
  findItem?: (fullyQualifiedItemIdentifier: string) => void;
  findGroup?: (fullyQualifiedGroupIdentifier: string) => void;
  isGroup: boolean;
}

/**
 * Returns all assessments of a given group or item if informations are available
 */
const LandscapeAssessment: React.FC<Props> = ({
  fullyQualifiedIdentifier,
  findItem,
  findGroup,
  isGroup,
}) => {
  const [group, setGroup] = useState<IGroup | null>();
  const [item, setItem] = useState<IItem | null>();

  const [assessmentGroup, setAssessmentGroup] = useState<IAssessmentResults | null>(null);
  const [assessmentItem, setAssessmentItem] = useState<IAssessmentProps[] | null>(null);

  useEffect(() => {
    get(`/api/${fullyQualifiedIdentifier}`).then((group) => {
      if (isGroup) {
        setGroup(group);
        setItem(null);
      } else {
        setItem(group);
        setGroup(null);
      }
    });

    const landscapeIdentifier = fullyQualifiedIdentifier.split('/');
    if (landscapeIdentifier[0]) {
      get(`/assessment/${landscapeIdentifier[0]}`).then((response) => {
        if (isGroup) {
          setAssessmentGroup(response.results);
        } else {
          setAssessmentItem(response.results[fullyQualifiedIdentifier]);
        }
      });
    }
  }, [fullyQualifiedIdentifier, isGroup]);

  const getGroupAssessments = () => {
    if (group && assessmentGroup) {
      if (group.items) {
        let assessmentItemColor = 'grey';
        return group.items.map((item) => {
          [assessmentItemColor] = getAssessmentSummaryColorAndMessage(
            assessmentGroup[item.fullyQualifiedIdentifier],
            item.identifier
          );
          return (
            <div key={item.fullyQualifiedIdentifier} className='item'>
              <span
                className='itemTitle'
                onClick={() => {
                  if (findItem) {
                    findItem(item.fullyQualifiedIdentifier);
                  }
                }}
              >
                {item.name || item.identifier}
              </span>
              <span className='status' style={{ backgroundColor: assessmentItemColor }}></span>
            </div>
          );
        });
      }
    }
    return [];
  };

  const getItemAssessments = () => {
    if (item && assessmentItem) {
      let assessmentItemColor = 'grey';
      return assessmentItem.map((item) => {
        if (!item.field.includes('summary.')) {
          assessmentItemColor = getAssessmentColor(item);
          return (
            <div key={item.field} className='item'>
              <span className='assessmentTitle'>{item.field}</span>
              <span className='itemMessage'>{item.message}</span>
              <span className='status' style={{ backgroundColor: assessmentItemColor }}></span>
            </div>
          );
        }
        return <React.Fragment key={item.field} />;
      });
    }
    return [];
  };

  let items: ReactElement[] = isGroup ? getGroupAssessments() : getItemAssessments();

  if (items.length) {
    return (
      <div className='assessmentContent'>
        <div className='header'>
          <span
            className='title'
            onClick={() => {
              if (findGroup && group) {
                findGroup(group.fullyQualifiedIdentifier);
              }
            }}
          >
            {item ? item.name || item.identifier : null}
            {group ? group.name || group.identifier : null}
          </span>
        </div>
        <div className='itemsContent'>
          <div className='items'>
            <div className='item'>
              <span className='itemLabel'>KPI</span>
              {item ? <span className='itemLabel'>Message</span> : null}
              <span className='statusLabel'>Status</span>
            </div>
            {items}
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className='assessmentError'>
      <span className='errorMessage'>Error Loading Assessments!</span>
      <span className='errorIdentifier'>{fullyQualifiedIdentifier} does not exist!</span>
    </div>
  );
};

export default LandscapeAssessment;
