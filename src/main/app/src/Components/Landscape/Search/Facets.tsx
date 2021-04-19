import React from 'react';
import { Table, TableBody, TableCell, TableRow } from "@material-ui/core";
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { Assessment } from "@material-ui/icons";

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

  const getLabel = (facet: IFacet) => {
    if (facet.dim.startsWith("kpi_"))
      return <div title={'KPI'}><Assessment /> {facet.dim.replace("kpi_", '')}</div>;

    return facet.dim;
  }
  const facetsHtml = facets.map((facet: IFacet) => (
    <TableRow key={facet.dim}>
      <TableCell style={{ width: '25%' }}>{getLabel(facet)}</TableCell>
      <TableCell>
        {facet.labelValues.map((lv) => (
          <Chip
            onClick={() => {
              addFacet(facet.dim, lv.label);
            }}
            variant={'outlined'}
            size={'small'}
            key={facet.dim + '' + lv.label}
            label={lv.label}
            avatar={<Avatar>{lv.value}</Avatar>}
          />
        ))}
      </TableCell>

    </TableRow>
  ));

  return (
    <Table aria-label={'facets table'} style={{ tableLayout: 'fixed' }}>
      <TableBody>{facetsHtml}</TableBody>
    </Table>
  );
};

export default Facets;
