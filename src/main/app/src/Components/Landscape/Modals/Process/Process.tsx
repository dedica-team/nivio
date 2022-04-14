import React, { useContext, useState } from 'react';

import { IProcess } from '../../../../interfaces';
import { getItem, getLabels, getLinks } from '../../Utils/utils';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import StatusChip from '../../../StatusChip/StatusChip';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import Typography from '@material-ui/core/Typography';
import IconButton from '@material-ui/core/IconButton';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import { LandscapeContext } from '../../../../Context/LandscapeContext';
import { Close } from '@material-ui/icons';

interface Props {
  process: IProcess;
  sticky?: boolean;
}

/**
 * Returns a chosen group if information is available
 */
const Process: React.FC<Props> = ({ process, sticky }) => {
  const componentClasses = componentStyles();
  const landscapeContext = useContext(LandscapeContext);
  const locateFunctionContext = useContext(LocateFunctionContext);
  const [visible, setVisible] = useState<boolean>(true);
  if (!visible) return null;
  const assessment = landscapeContext.getAssessmentSummary(process.fullyQualifiedIdentifier);
  const labels = getLabels(process);
  const links = getLinks(process);

  return (
    <Card className={componentClasses.card}>
      <CardHeader
        title={
          <React.Fragment>
            <IconButton
              onClick={() => locateFunctionContext.locateFunction(process.fullyQualifiedIdentifier)}
              size={'small'}
              title={`Click to locate process ${process.identifier}`}
            />
            &nbsp;{process.name || process.identifier}
          </React.Fragment>
        }
        className={componentClasses.cardHeader}
        action={
          <React.Fragment>
            {!sticky ? (
              <IconButton
                size={'small'}
                onClick={() => {
                  setVisible(false);
                }}
              >
                <Close />
              </IconButton>
            ) : null}
          </React.Fragment>
        }
      />
      <CardContent>
        <div className='information'>
          <span className='description group'>
            {process?.description ? `${process?.description}` : ''}
          </span>
          {process?.contact ? (
            <span className='contact group'>
              <span className='label'>Contact: </span>
              {process?.contact || 'No Contact provided'}
            </span>
          ) : null}
          <div className='owner group'>
            <span className='label'>Owner: </span>
            {process?.owner || 'No Owner provided'}
          </div>
        </div>

        {assessment && assessment.status ? (
          <div>
            <div>
              <br />
              <Typography variant={'h6'}>Status</Typography>
              <StatusChip name={assessment.field} status={assessment?.status} />
              {assessment.message.split('; ').map((message) => {
                //TODO improve
                const uriAndMessages = message.trimLeft().split(' ');
                const fqi = uriAndMessages.shift() || '';
                const item = landscapeContext.landscape
                  ? getItem(landscapeContext.landscape, fqi)
                  : null;
                return (
                  <div key={message}>
                    <strong>{item ? item.identifier : ''}</strong> {uriAndMessages.join(' ')}
                  </div>
                );
              })}
            </div>
            <br />
            <br />
          </div>
        ) : null}

        <div className='labels'>{labels}</div>

        {links.length ? (
          <div className='linkContent'>
            <span className='linkLabel'>Links</span>
            <div className='links'>{links}</div>
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
};

export default Process;
