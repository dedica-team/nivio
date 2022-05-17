import React, { useContext, useEffect, useState } from 'react';

import { IGroup } from '../../../../interfaces';
import { getGroup, getLabels, getLinks } from '../../Utils/utils';
import { Card, CardHeader } from '@material-ui/core';
import CardContent from '@material-ui/core/CardContent';
import StatusChip from '../../../StatusChip/StatusChip';
import componentStyles from '../../../../Resources/styling/ComponentStyles';
import Typography from '@material-ui/core/Typography';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import { LocateFunctionContext } from '../../../../Context/LocateFunctionContext';
import ItemAvatar from '../Item/ItemAvatar';
import GroupAvatar from './GroupAvatar';
import { LandscapeContext } from '../../../../Context/LandscapeContext';
import { Close } from '@material-ui/icons';

interface Props {
  initialGroup: IGroup;
  sticky?: boolean;
}

/**
 * Returns a chosen group if information is available
 */
const Group: React.FC<Props> = ({ initialGroup, sticky }) => {
  const componentClasses = componentStyles();
  const landscapeContext = useContext(LandscapeContext);
  const locateFunctionContext = useContext(LocateFunctionContext);
  const [visible, setVisible] = useState<boolean>(true);
  const [group, setGroup] = useState<IGroup>(initialGroup);

  const getGroupItems = (
    group: IGroup,
    findItem?: (fullyQualifiedItemIdentifier: string) => void
  ) => {
    if (group?.items) {
      return group.items.map((item) => {
        const itemStatus = landscapeContext.getAssessmentSummary(item.fullyQualifiedIdentifier);
        if (!itemStatus || !itemStatus?.status) return null;
        return (
          <Button
            style={{ textAlign: 'left' }}
            key={item.fullyQualifiedIdentifier}
            onClick={() => {
              if (findItem) {
                findItem(item.fullyQualifiedIdentifier);
              }
            }}
          >
            <ItemAvatar
              item={item}
              statusColor={itemStatus.status === 'UNKNOWN' ? 'WHITE' : itemStatus.status}
            />
            &nbsp;
            {item.identifier}
          </Button>
        );
      });
    }
    return [];
  };

  useEffect(() => {
    if (landscapeContext.landscape) {
      const newGroup = getGroup(landscapeContext.landscape, initialGroup.fullyQualifiedIdentifier);
      if (newGroup) {
        newGroup.items.length > 0 ? setGroup(newGroup) : setVisible(false);
      } else {
        setVisible(false);
      }
    }
  }, [landscapeContext.landscape, initialGroup]);

  if (!visible) return null;

  const items = getGroupItems(group, locateFunctionContext.locateFunction);

  const assessment = landscapeContext.getAssessmentSummary(group.fullyQualifiedIdentifier);
  const labels = getLabels(group);
  const links = getLinks(group);

  return (
    <Card className={componentClasses.card}>
      <CardHeader
        title={
          <React.Fragment>
            <IconButton
              onClick={() => locateFunctionContext.locateFunction(group.fullyQualifiedIdentifier)}
              size={'small'}
              title={`Click to local group ${group.identifier}`}
            >
              <GroupAvatar group={group} statusColor={assessment ? assessment.status : ''} />
            </IconButton>
            &nbsp;{group.name}
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
            {group?.description ? `${group?.description}` : ''}
          </span>
          {group?.contact ? (
            <div className='contact group'>
              <span className='label'>Contact: </span>
              {group?.contact || 'No Contact provided'}
            </div>
          ) : null}
          <div className='owner group'>
            <span className='label'>Owner: </span>
            {group?.owner || 'No Owner provided'}
          </div>
        </div>

        {assessment && assessment.status && assessment.status !== 'UNKNOWN' ? (
          <div>
            <div>
              <br />
              <Typography variant={'h6'}>Status</Typography>
              <StatusChip name={assessment.field} status={assessment?.status} />
              <ul>
                {assessment.message.split(';').map((message) => {
                  return message && message.length ? (
                    <li style={{ textAlign: 'left' }} key={message}>
                      {message}
                    </li>
                  ) : null;
                })}
              </ul>
            </div>
            <br />
            <br />
          </div>
        ) : (
          <br />
        )}

        <div className='labels'>{labels}</div>

        {links.length ? (
          <div className='linkContent'>
            <span className='linkLabel'>Links</span>
            <div className='links'>{links}</div>
          </div>
        ) : null}

        {items.length ? (
          <div className='itemsContent'>
            <Typography variant={'h6'}>Items</Typography>
            {items}
          </div>
        ) : null}
      </CardContent>
    </Card>
  );
};

export default Group;
