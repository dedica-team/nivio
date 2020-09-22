import React from 'react';
import MuiAlert, { AlertProps } from '@material-ui/lab/Alert';
import Button from '@material-ui/core/Button';
import Snackbar from '@material-ui/core/Snackbar';
import IconButton from '@material-ui/core/IconButton';
import CloseIcon from '@material-ui/icons/Close';

import { ISnackbarMessage } from '../../interfaces';
import './Notification.scss';

import { Link } from 'react-router-dom';

function Alert(props: AlertProps) {
  return <MuiAlert elevation={6} variant='filled' {...props} />;
}

interface Props {
  messageInfo: ISnackbarMessage | undefined;
  open: boolean;
  snackPackCloseDelay: number;
  handleClose: (event?: React.SyntheticEvent, reason?: string) => void;
  handleExited: () => void;
}

/**
 * Displays a notification if a file change is detected
 */
const NotificationLayout: React.FC<Props> = ({
  messageInfo,
  snackPackCloseDelay,
  open,
  handleClose,
  handleExited,
}) => {
  return (
    <Snackbar
      key={messageInfo ? messageInfo.key : undefined}
      open={open}
      autoHideDuration={snackPackCloseDelay}
      onClose={handleClose}
      onExited={handleExited}
    >
      <Alert
        onClose={handleClose}
        severity={messageInfo?.level}
        action={
          messageInfo?.landscape !== 'unknown' ? (
            <React.Fragment>
              <Button
                component={Link}
                className={'mapButton'}
                data-testid='MapButton'
                to={`/landscape/${messageInfo?.landscape}`}
              >
                Show Map
              </Button>
              <IconButton aria-label='close' color='inherit' onClick={handleClose}>
                <CloseIcon />
              </IconButton>
            </React.Fragment>
          ) : undefined
        }
      >
        {messageInfo ? messageInfo.message : undefined}
      </Alert>
    </Snackbar>
  );
};

export default NotificationLayout;
