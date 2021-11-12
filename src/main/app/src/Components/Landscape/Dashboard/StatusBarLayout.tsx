import React, { useContext } from 'react';
import { IGroup } from '../../../interfaces';
import StatusChip from '../../StatusChip/StatusChip';
import Button from '@material-ui/core/Button';
import {
  AppBar,
  Box,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Tabs,
  Typography,
} from '@material-ui/core';
import { LandscapeContext } from '../../../Context/LandscapeContext';
import { Settings, Warning } from '@material-ui/icons';
import ItemAvatar from '../Modals/Item/ItemAvatar';
import GroupAvatar from '../Modals/Group/GroupAvatar';
import { a11yProps, TabPanel } from '../Utils/TabUtils';
import KPIConfigLayout from './KPIConfigLayout';

interface Props {
  onItemClick: Function;
  onGroupClick: Function;
}

/**
 * Displays all groups of given landscape and provides all needed navigation
 */
const StatusBarLayout: React.FC<Props> = ({ onItemClick, onGroupClick }) => {
  const context = useContext(LandscapeContext);
  const [currentTab, setCurrentTab] = React.useState(0);

  const getItems = (group: IGroup) => {
    return group.items.map((item) => {
      const assessmentSummary = context.getAssessmentSummary(item.fullyQualifiedIdentifier);

      if (assessmentSummary?.field === '') return null;
      if (
        !assessmentSummary?.status ||
        assessmentSummary?.status === 'GREEN' ||
        assessmentSummary.status === 'UNDEFINED'
      )
        return null;

      return (
        <TableRow key={'status_' + item.fullyQualifiedIdentifier}>
          <TableCell style={{ textAlign: 'center' }}>
            <Button
              key={item.fullyQualifiedIdentifier}
              title={item.name || item.identifier}
              onClick={() => onItemClick(item)}
            >
              <ItemAvatar item={item} statusColor={assessmentSummary?.status} />
            </Button>
            <br />
            {item.name || item.identifier}
          </TableCell>
          <TableCell>
            <StatusChip name={assessmentSummary.field} status={assessmentSummary.status} />
            {assessmentSummary.message}
          </TableCell>
        </TableRow>
      );
    });
  };

  const getGroups = (groups: IGroup[]) => {
    if (!groups) return;

    return groups.map((group) => {
      if (group.items.length === 0) {
        return null;
      }

      const groupAssessment = context.getAssessmentSummary(group.fullyQualifiedIdentifier);
      if (
        !groupAssessment ||
        !groupAssessment?.status ||
        groupAssessment?.status === 'GREEN' ||
        groupAssessment.status === 'UNDEFINED'
      )
        return null;

      if (groupAssessment.field === '') {
        console.debug('Group ' + group.fullyQualifiedIdentifier + ' has no summary assessment');
        return null;
      }

      return (
        <TableRow key={'status_' + group.fullyQualifiedIdentifier}>
          <TableCell style={{ textAlign: 'center' }}>
            <Button
              id={group.fullyQualifiedIdentifier}
              onClick={() => onGroupClick(group)}
              key={group.name}
            >
              <GroupAvatar group={group} statusColor={groupAssessment.status} />
            </Button>
            <br />
            {group.name}
          </TableCell>
          <TableCell>
            <StatusChip
              name={`${groupAssessment.identifier}: ${groupAssessment.field}`}
              status={groupAssessment.status}
            />
            {groupAssessment.message}
          </TableCell>
        </TableRow>
      );
    });
  };

  const changeTab = (event: React.ChangeEvent<{}>, newValue: number) => {
    setCurrentTab(newValue);
  };

  const kpiConfig = context.landscape?.kpis;
  let kpis: JSX.Element[] = [];
  if (kpiConfig) {
    for (let key of Object.keys(kpiConfig)) {
      const kpiConfigElement = kpiConfig[key];
      if (kpiConfigElement.enabled) {
        kpis.push(<KPIConfigLayout kpi={kpiConfigElement} name={key} key={key} />);
      }
    }
  }

  return (
    <Box>
      <div>
        <Typography variant={'h5'}>Status</Typography>
      </div>
      <AppBar position={'static'}>
        <Tabs
          value={currentTab}
          onChange={changeTab}
          variant={'fullWidth'}
          aria-label={'item tabs'}
        >
          <Tab
            icon={<Warning />}
            label={'warnings'}
            style={{ minWidth: 50 }}
            title={'Warnings'}
            {...a11yProps(0, 'statusbar')}
          />
          <Tab
            icon={<Settings />}
            label={'kpis'}
            style={{ minWidth: 50 }}
            title={'KPIs'}
            {...a11yProps(1, 'statusbar')}
          />
        </Tabs>
      </AppBar>
      <TabPanel value={currentTab} index={0} prefix={'statusbar'}>
        <Table>
          <TableBody>
            {context.landscape ? getGroups(context.landscape.groups) : null}
            {context.landscape?.groups.map((group) => getItems(group))}
          </TableBody>
        </Table>
      </TabPanel>
      <TabPanel value={currentTab} index={1} prefix={'statusbar'}>
        {kpis}
      </TabPanel>
    </Box>
  );
};

export default StatusBarLayout;
