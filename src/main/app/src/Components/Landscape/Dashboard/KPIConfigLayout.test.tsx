import { render } from '@testing-library/react';
import React from 'react';
import KPIConfigLayout from './KPIConfigLayout';
import { IKpi } from '../../../interfaces';

describe('<KPIConfigLayout />', () => {
  const kpi: IKpi = {
    description:
      "Turns yellow if the 'scale' label is zero, orange if it is a data sink, and red if it is a provider.",
    enabled: true,
    ranges: {
      GREEN: {
        maximum: 'Infinity',
        minimum: '1.0',
      },
      YELLOW: {
        description: 'scaled to zero',
        maximum: '0.0',
        minimum: '0.0',
      },
      ORANGE: {
        description: 'data sink scaled to zero',
        maximum: '0.0',
        minimum: '0.0',
      },
      RED: {
        description: 'provider scaled to zero',
        maximum: '0.0',
        minimum: '0.0',
      },
    },
    matches: {
      GREEN: ['All conditions are met.'],
      RED: ['One condition is not met.'],
    },
  };

  it('should display', () => {
    const { getByText, queryByText } = render(<KPIConfigLayout name={'fooKPI'} kpi={kpi} />);
    expect(getByText('fooKPI')).toBeInTheDocument();
    expect(getByText('All conditions are met.')).toBeInTheDocument();
    expect(getByText('[1.0 → Infinity]')).toBeInTheDocument();
  });
});
