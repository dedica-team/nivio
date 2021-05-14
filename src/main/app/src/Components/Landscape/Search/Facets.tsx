import React from 'react';
import { AppBar, Box, Tab, Table, TableBody, TableCell, TableRow, Tabs, TextField } from "@material-ui/core";
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { ListAlt, SaveAlt, Speed } from '@material-ui/icons';
import Button from "@material-ui/core/Button";
import { SaveSearchConfig } from "./SaveSearchConfig";

interface IFacet {
  dim: string;
  path: [];
  value: number;
  childCount: number;
  labelValues: ILabelValue[];
}

interface FacetsInterface {
  addFacet: (dim: string, label: string) => string;
  saveSearch: (config: SaveSearchConfig) => void;
  facets: IFacet[];
}

interface ILabelValue {
  label: string;
  value: number;
}

const Facets: React.FC<FacetsInterface> = ({ facets, addFacet, saveSearch }) => {
  const facetsHtml: JSX.Element[] = [];
  const kpiHtml: JSX.Element[] = [];
  const [value, setValue] = React.useState(0);

  const a11yProps = (index: any) => {
    return {
      'id': `search-tab-${index}`,
      'aria-controls': `search-tabpanel-${index}`,
    };
  };

  const changeTab = (event: React.ChangeEvent<{}>, newValue: number) => {
    setValue(newValue);
  };

  interface TabPanelProps {
    children?: React.ReactNode;
    index: any;
    value: any;
  }

  function TabPanel(props: TabPanelProps) {
    const { children, value, index, ...other } = props;

    return (
      <div
        role='tabpanel'
        hidden={value !== index}
        id={`search-tabpanel-${index}`}
        aria-labelledby={`search-tab-${index}`}
        {...other}
      >
        {value === index && <Box>{children}</Box>}
      </div>
    );
  }

  //regular facets
  facets
    .filter((facet: IFacet) => !facet.dim.startsWith('kpi_'))
    .forEach((facet: IFacet) =>
      facetsHtml.push(
        <TableRow key={facet.dim}>
          <TableCell style={{ width: '25%' }}>{facet.dim}</TableCell>
          <TableCell>
            {facet.labelValues.map((lv) => (
              <Chip
                onClick={() => {
                  addFacet(facet.dim, lv.label);
                }}
                variant={'default'}
                color={'primary'} style={{margin: 1}}
                size={'small'}
                key={facet.dim + '' + lv.label}
                label={lv.label}
                avatar={<Avatar>{lv.value}</Avatar>}
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
            {facet.labelValues.map((lv) => (
              <Chip
                onClick={() => {
                  addFacet(facet.dim, lv.label);
                }}
                variant={'default'}
                size={'small'}
                key={facet.dim + '' + lv.label}
                label={lv.label}
                style={{ backgroundColor: lv.label, color: 'black', margin: 1 }}
                avatar={<Avatar>{lv.value}</Avatar>}
              />
            ))}
          </TableCell>
        </TableRow>
      )
    );

  const exportCurrent = () => {
    const elementById = document.getElementById('report-title');
    // @ts-ignore
    const title = elementById != null ? elementById.value :'';
    saveSearch({reportType: 'owners', title: title})
  }

  return (
    <>
      <br />
      <br />
      <AppBar position={'static'}>
        <Tabs value={value} onChange={changeTab} variant={'fullWidth'} aria-label={'search tabs'}>
          <Tab
            icon={<ListAlt />}
            label={'fields'}
            style={{ minWidth: 50 }}
            title={'Fields'}
            {...a11yProps(0)}
          />
          <Tab
            icon={<Speed />}
            label={'kpis'}
            style={{ minWidth: 50 }}
            title={'KPIs'}
            {...a11yProps(1)}
          />
          <Tab
            icon={<SaveAlt />}
            label={'Save'}
            title={'Save'}
            style={{ minWidth: 50 }}
            {...a11yProps(2)}
          />
        </Tabs>
      </AppBar>
      <br />

      <TabPanel value={value} index={0}>
        <Table aria-label={'regular facets'} style={{ tableLayout: 'fixed' }}>
          <TableBody>{facetsHtml}</TableBody>
        </Table>
      </TabPanel>

      <TabPanel value={value} index={1}>
        <Table aria-label={'kpi facets'} style={{ tableLayout: 'fixed' }}>
          <TableBody>{kpiHtml}</TableBody>
        </Table>
      </TabPanel>

      <TabPanel value={value} index={2}>
        <TextField id="report-title" label="Report title" variant="standard" fullWidth={true} /><br />
        <br/>
        <Button title={'Export'} fullWidth={true} onClick={() => exportCurrent()} variant={"outlined"}>Export as report</Button>
      </TabPanel>

    </>
  );
};

export default Facets;
