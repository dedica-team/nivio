import React from 'react';
import {
  AppBar,
  Tab,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Tabs,
  TextField,
} from '@material-ui/core';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { ListAlt, Print, Speed } from '@material-ui/icons';
import Button from '@material-ui/core/Button';
import { SaveSearchConfig } from './SaveSearchConfig';
import { IFacet } from '../../../interfaces';
import { a11yProps, TabPanel } from '../Utils/TabUtils';

interface FacetsProps {
  addFacet: (dim: string, label: string) => string;
  saveSearch: (config: SaveSearchConfig) => void;
  facets: IFacet[];
}

const Facets: React.FC<FacetsProps> = ({ facets, addFacet, saveSearch }) => {
  const facetsHtml: JSX.Element[] = [];
  const kpiHtml: JSX.Element[] = [];
  const [value, setValue] = React.useState(0);

  const changeTab = (event: React.ChangeEvent<{}>, newValue: number) => {
    setValue(newValue);
  };

  //regular facets
  facets
    .filter((facet: IFacet) => !facet.dim.startsWith('kpi_'))
    .forEach((facet: IFacet) =>
      facetsHtml.push(
        <TableRow key={facet.dim}>
          <TableCell style={{ width: '25%' }}>{facet.dim}</TableCell>
          <TableCell>
            {facet.labelValues.map((cv) => (
              <Chip
                onClick={() => {
                  addFacet(facet.dim, cv.label);
                }}
                variant={'default'}
                color={'primary'}
                style={{ margin: 1, backgroundColor: cv.color }}
                size={'small'}
                key={facet.dim + '' + cv.label}
                label={cv.label}
                avatar={<Avatar>{cv.value}</Avatar>}
              />
            ))}
          </TableCell>
        </TableRow>
      )
    );

  const getLabel = (facet: IFacet) => {
    if (facet.dim.startsWith('kpi_'))
      return <div title={'KPI'}>{facet.dim.replace('kpi_', '')}</div>;

    return facet.dim;
  };

  //kpis
  facets
    .filter((facet: IFacet) => facet.dim.startsWith('kpi_'))
    .forEach((facet: IFacet) =>
      kpiHtml.push(
        <TableRow key={facet.dim}>
          <TableCell style={{ width: '35%' }}>{getLabel(facet)}</TableCell>
          <TableCell>
            {facet.labelValues.map((cv) => (
              <Chip
                onClick={() => {
                  addFacet(facet.dim, cv.label);
                }}
                variant={'default'}
                size={'small'}
                key={facet.dim + '' + cv.label}
                label={cv.label}
                style={{ backgroundColor: cv.label, color: 'black', margin: 1 }}
                avatar={<Avatar>{cv.value}</Avatar>}
              />
            ))}
          </TableCell>
        </TableRow>
      )
    );

  const exportCurrent = () => {
    const elementById = document.getElementById('report-title');
    // @ts-ignore
    const title = elementById != null ? elementById.value : '';
    saveSearch({ reportType: 'owners', title: title });
  };

  return (
    <>
      <AppBar position={'static'}>
        <Tabs value={value} onChange={changeTab} variant={'fullWidth'} aria-label={'search tabs'}>
          <Tab
            icon={<ListAlt />}
            label={'fields'}
            style={{ minWidth: 50 }}
            title={'Fields'}
            {...a11yProps(0, 'search')}
          />
          <Tab
            icon={<Speed />}
            label={'kpis'}
            style={{ minWidth: 50 }}
            title={'KPIs'}
            {...a11yProps(1, 'search')}
          />
          <Tab
            icon={<Print />}
            label={'Report'}
            title={'Export current search as report'}
            style={{ minWidth: 50 }}
            {...a11yProps(2, 'search')}
          />
        </Tabs>
      </AppBar>
      <br />

      <TabPanel value={value} index={0} prefix={'search'}>
        <Table aria-label={'regular facets'} style={{ tableLayout: 'fixed' }}>
          <TableBody>{facetsHtml}</TableBody>
        </Table>
      </TabPanel>

      <TabPanel value={value} index={1} prefix={'search'}>
        <Table aria-label={'kpi facets'} style={{ tableLayout: 'fixed' }}>
          <TableBody>{kpiHtml}</TableBody>
        </Table>
      </TabPanel>

      <TabPanel value={value} index={2} prefix={'search'}>
        <TextField id='report-title' label='Report title' variant='standard' fullWidth={true} />
        <br />
        <br />
        <Button
          title={'Export as report'}
          fullWidth={true}
          onClick={() => exportCurrent()}
          variant={'outlined'}
        >
          Export as report
        </Button>
      </TabPanel>
    </>
  );
};

export default Facets;
