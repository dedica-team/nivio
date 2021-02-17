import React from 'react';
import { render } from '@testing-library/react';
import Notification from './Notification';
import NotificationLayout from './NotificationLayout';

import { MemoryRouter } from 'react-router-dom';

jest.mock('@stomp/stompjs');

it('should notification component', () => {
  const { getByTestId } = render(
    <MemoryRouter>
      <Notification />
    </MemoryRouter>
  );
  expect(getByTestId('notification')).toBeInTheDocument();
});

it('should remove show map button when landscape is unknown', () => {
  const handleClose = () => {};
  const handleExited = () => {};
  const open = true;
  const snackPackCloseDelay = 2000;
  const messageInfo = { message: 'Test Message', key: 123, landscape: '', level: undefined };

  const { queryByText } = render(
    <MemoryRouter>
      <NotificationLayout
        handleClose={handleClose}
        handleExited={handleExited}
        messageInfo={messageInfo}
        open={open}
        snackPackCloseDelay={snackPackCloseDelay}
      ></NotificationLayout>
    </MemoryRouter>
  );

  expect(queryByText('Show Map')).not.toBeTruthy();
});

it('should contain show map button when landscape is set', () => {
  const handleClose = () => {};
  const handleExited = () => {};
  const open = true;
  const snackPackCloseDelay = 2000;
  const messageInfo = {
    message: 'Test Message',
    key: 123,
    landscape: 'testLandscape',
    level: undefined,
  };

  const { queryByText } = render(
    <MemoryRouter>
      <NotificationLayout
        handleClose={handleClose}
        handleExited={handleExited}
        messageInfo={messageInfo}
        open={open}
        snackPackCloseDelay={snackPackCloseDelay}
      ></NotificationLayout>
    </MemoryRouter>
  );

  expect(queryByText('Show Map')).toBeTruthy();
});

it('should correctly link to map', () => {
  const handleClose = () => {};
  const handleExited = () => {};
  const open = true;
  const snackPackCloseDelay = 2000;
  const messageInfo = {
    message: 'Test Message',
    key: 123,
    landscape: 'testLandscape',
    level: undefined,
  };

  const { getByTestId } = render(
    <MemoryRouter>
      <NotificationLayout
        handleClose={handleClose}
        handleExited={handleExited}
        messageInfo={messageInfo}
        open={open}
        snackPackCloseDelay={snackPackCloseDelay}
      ></NotificationLayout>
    </MemoryRouter>
  );

  expect(getByTestId('MapButton')).toHaveAttribute('href', `/landscape/${messageInfo.landscape}`);
});
