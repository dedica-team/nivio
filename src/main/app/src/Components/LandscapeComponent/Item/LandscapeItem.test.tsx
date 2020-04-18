import React from 'react';
import { render } from '@testing-library/react';
import LandscapeItem from './LandscapeItem';

jest.mock('react-console-emulator');

it('should render log component', () => {
  const element = document.createElement('div');
  const { getByText } = render(<LandscapeItem element={element} host={process.env.PUBLIC_URL} />);
  expect(getByText('Not Found :(')).toBeInTheDocument();
});
