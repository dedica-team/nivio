import React from 'react';
import { render } from '@testing-library/react';
import App from './App';

jest.mock('react-console-emulator');

it('should render home component', () => {
  const { getByText } = render(<App />);
  expect(getByText('Loading landscapes...')).toBeInTheDocument();
});
