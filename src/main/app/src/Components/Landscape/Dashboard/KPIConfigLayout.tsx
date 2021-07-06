import React from 'react';
import { IKpi } from '../../../interfaces';
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Box,
  Table,
  TableBody,
  TableCell,
  TableRow,
} from '@material-ui/core';
import Typography from '@material-ui/core/Typography';
import StatusChip from '../../StatusChip/StatusChip';
import { ExpandMore } from '@material-ui/icons';

interface Props {
  name: string;
  kpi: IKpi;
}

/**
 * Displays the configuration of a single KPI.
 *
 * @param name kpi name
 * @param kpi
 * @constructor
 */
const KPIConfigLayout: React.FC<Props> = ({ name, kpi }) => {
  let ranges: JSX.Element[] = [];
  if (kpi.ranges) {
    for (let key of Object.keys(kpi.ranges)) {
      const range = kpi.ranges[key];
      const rangeText =
        range.minimum == range.maximum ? range.minimum : `${range.minimum} → ${range.maximum}`;
      ranges.push(
        <TableRow key={'range_' + key}>
          <TableCell>
            <StatusChip name={name} status={key} />
          </TableCell>
          <TableCell>
            <Typography>
              {`[${rangeText}]`} {range.description ?? ''}
            </Typography>
          </TableCell>
        </TableRow>
      );
    }
  }
  let matches: JSX.Element[] = [];
  if (kpi.matches) {
    for (let key of Object.keys(kpi.matches)) {
      const matchers = kpi.matches[key];
      matches.push(
        <TableRow key={'matches_' + key}>
          <TableCell>
            <StatusChip name={name} status={key} />
          </TableCell>
          <TableCell>
            <Typography>{matchers.join(', ')}</Typography>
          </TableCell>
        </TableRow>
      );
    }
  }

  return (
    <Box key={`kpi_${kpi.label}`}>
      <Accordion key={name} style={{ boxShadow: 'none' }}>
        <AccordionSummary
          style={{ padding: 2 }}
          expandIcon={<ExpandMore />}
          aria-controls={'panel_kpi' + name + 'bh-content'}
          id={'panel_kpi' + name + 'bh-header'}
        >
          <Typography variant={'h6'}>{name}</Typography>
        </AccordionSummary>
        <AccordionDetails style={{ display: 'block', padding: 2 }}>
          <div style={{ width: '100%' }}>{kpi.description}</div>
          <br />

          {ranges.length ? (
            <Table>
              <TableBody>{ranges}</TableBody>
            </Table>
          ) : null}
          {matches.length ? (
            <Table>
              <TableBody>{matches}</TableBody>
            </Table>
          ) : null}
        </AccordionDetails>
      </Accordion>
    </Box>
  );
};
export default KPIConfigLayout;
