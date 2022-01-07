import * as React from 'react';
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
} from '@material-ui/core';
import { LoginButtons } from './LoginButtons';
import IconButton from "@material-ui/core/IconButton";
import { Person } from "@material-ui/icons";
import { useContext } from "react";
import { OAuth2LinksContext } from "../../Context/OAuth2LinksContext";

export default function LoginDialog() {
  const [open, setOpen] = React.useState(false);
  const oAuth2Links = useContext(OAuth2LinksContext);

  const handleClickOpen = () => {
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  if (Object.keys(oAuth2Links.oAuth2Link).length === 0) {
    return null;
  }

  return (
    <React.Fragment>
      <IconButton onClick={handleClickOpen}>
        <Person/>
      </IconButton>
      <Dialog fullWidth={false} maxWidth={'xl'} open={open} onClose={handleClose}>
        <DialogTitle>Login options</DialogTitle>
        <DialogContent>
          <DialogContentText>Please select a login option</DialogContentText>
          <LoginButtons />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClose}>Close</Button>
        </DialogActions>
      </Dialog>
    </React.Fragment>
  );
}
