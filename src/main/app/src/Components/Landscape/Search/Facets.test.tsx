import { fireEvent, getByTitle, render } from '@testing-library/react';
import React from 'react';
import Facets from './Facets';
import { IFacet } from '../../../interfaces';

describe('<Facets />', () => {
  const facets: IFacet[] = [
    {
      dim: 'kpi_security',
      value: 19,
      labelValues: [
        {
          label: 'green',
          value: 12,
        },
        {
          label: 'red',
          value: 7,
        },
      ],
    },
    {
      dim: 'owner',
      value: 12,
      labelValues: [
        {
          label: 'foo',
          value: 5,
        },
        {
          label: 'bar',
          value: 7,
        },
      ],
    },
  ];

  const addFacet = jest.fn((dim: string, label: string) => dim + ' ' + label);
  const saveSearch = jest.fn();

  it('should display fields', () => {
    const { getByText } = render(
      <Facets facets={facets} addFacet={addFacet} saveSearch={saveSearch} />
    );
    expect(getByText('owner')).toBeInTheDocument();
    expect(getByText('foo')).toBeInTheDocument();
    expect(getByText('bar')).toBeInTheDocument();
  });

  it('should display kpis', () => {
    const { container, queryByText, getByText } = render(
      <Facets facets={facets} addFacet={addFacet} saveSearch={saveSearch} />
    );
    fireEvent.click(getByTitle(container, 'KPIs'));

    expect(queryByText('owner')).toBeNull();
    expect(getByText('security')).toBeInTheDocument();
    expect(getByText('green')).toBeInTheDocument();
    expect(getByText('red')).toBeInTheDocument();
  });

  it('should display export button', () => {
    const { container, getByText } = render(
      <Facets facets={facets} addFacet={addFacet} saveSearch={saveSearch} />
    );

    fireEvent.click(getByTitle(container, 'Export current search as report'));
    expect(getByText('Export as report')).toBeInTheDocument();
  });

  it('should export as report', () => {
    const { container, getByText } = render(
      <Facets facets={facets} addFacet={addFacet} saveSearch={saveSearch} />
    );

    fireEvent.click(getByTitle(container, 'Export current search as report'));
    expect(getByText('Report')).toBeInTheDocument();

    fireEvent.click(getByTitle(container, 'Export as report'));
    expect(saveSearch.mock.calls.length).toBe(1);
  });

  it('should add facet', () => {
    const { getByText } = render(
      <Facets facets={facets} addFacet={addFacet} saveSearch={saveSearch} />
    );

    fireEvent.click(getByText('foo'));
    expect(addFacet.mock.calls.length).toBe(1);
  });
});
