import React from 'react';
import { Table, TableBody, TableCell, TableRow, Typography } from '@material-ui/core';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';

interface IFacet {
  dim: string;
  path: [];
  value: number;
  childCount: number;
  labelValues: ILabelValue[];
}

interface FacetsInterface {
  addFacet: Function;
  facets: IFacet[];
}

interface ILabelValue {
  label: string;
  value: number;
}

const Facets: React.FC<FacetsInterface> = ({ facets, addFacet }) => {
  const facetsHtml: JSX.Element[] = [];
  const kpiHtml: JSX.Element[] = [];

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

  return (
    <>
      <Typography variant={'h6'}>Field filters</Typography>
      <Table aria-label={'regular facets'} style={{ tableLayout: 'fixed' }}>
        <TableBody>{facetsHtml}</TableBody>
      </Table>
      <br />
      <Typography variant={'h6'}>KPIs</Typography>
      <Table aria-label={'kpi facets'} style={{ tableLayout: 'fixed' }}>
        <TableBody>{kpiHtml}</TableBody>
      </Table>
    </>
  );
};

export default Facets;
