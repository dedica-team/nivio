import * as React from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@material-ui/core';
import { GithubLoginButton } from 'react-social-login-buttons';

export default function LoginDialog() {
  const [open, setOpen] = React.useState(false);
  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  return (
    <React.Fragment>
      <Button variant='outlined' onClick={handleClickOpen}>
        Login
      </Button>
      <Dialog fullWidth={false} maxWidth={'xl'} open={open} onClose={handleClose}>
        <DialogTitle>Login options</DialogTitle>
        <DialogContent>
          <DialogContentText>Please select a login option</DialogContentText>
          <a href={`/oauth2/authorization/github`}>
            <GithubLoginButton />
          </a>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Close</Button>
        </DialogActions>
      </Dialog>
    </React.Fragment>
  );
}
