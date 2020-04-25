import React from 'react';
import { render, fireEvent } from '@testing-library/react';
import GenericModal from './GenericModal';

jest.mock('react-console-emulator');

it('should render log component', () => {
  const element = 'Test';
  const { getByText } = render(<GenericModal modalContent={element} />);
  expect(getByText('Test')).toBeInTheDocument();
});

it('should close on click', () => {
  const element = 'Test';
  const { queryByText, getByText } = render(<GenericModal modalContent={element} />);

  expect(getByText('Test')).toBeInTheDocument();

  fireEvent.click(getByText('close'));

  expect(queryByText('Test')).toBeNull();
});
