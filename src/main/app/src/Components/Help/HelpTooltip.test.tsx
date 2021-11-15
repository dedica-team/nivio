import React from 'react';

import '@testing-library/jest-dom/extend-expect';
import HelpTooltip from './HelpTooltip';
import { fireEvent, render, waitFor } from '@testing-library/react';

it('should render HelpTooltip with given content', () => {
  const baseDom = render(<HelpTooltip content={<div>foo</div>} />);
  const icon = baseDom.getByLabelText('help');
  fireEvent.mouseOver(icon);

  waitFor(() => expect(baseDom.findByText('foo')).toBeInTheDocument());
});
