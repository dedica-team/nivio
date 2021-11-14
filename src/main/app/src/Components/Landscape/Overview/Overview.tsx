import React, { useCallback, useEffect, useState } from "react";

import {ILandscape, ILandscapeLinks} from '../../../interfaces';
import OverviewLayout from './OverviewLayout';
import {get} from '../../../utils/API/APIClient';
import {createStyles, darken, Theme} from '@material-ui/core';
import {Redirect} from 'react-router-dom';
import makeStyles from '@material-ui/core/styles/makeStyles';
import {withBasePath} from '../../../utils/API/BasePath';
import Avatar from '@material-ui/core/Avatar';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    loading: {
      position: 'absolute',
      width: '100vw',
      height: '100vh',
      verticalAlign: 'center',
      top: 0,
      left: 0,
      zIndex: 1000,
      backgroundColor: darken(theme.palette.primary.main, 0.2),
    },
    loadingLogo: {
      'position': 'absolute',
      'top': '50%',
      'left': '50%',
      '-webkit-transform': 'translate(-50%, -50%)',
      'transform': 'translate(-50%, -50%)',
    },
    large: {
      width: theme.spacing(25),
      height: theme.spacing(25),
    },
  })
);


interface Props {
  setPageTitle: Function;
  welcomeMessage: string;
}

/**
 * Logic Component to display all available landscapes
 */
const Overview: React.FC<Props> = ({ setPageTitle, welcomeMessage }) => {
  const [landscapes, setLandscapes] = useState<ILandscape[]>([]);
  const [landscapeLinks, setLandscapeLinks] = useState<ILandscapeLinks | null>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);
  const [landscapesCount, setLandscapesCount] = useState<Number>(0);
  const classes = useStyles();

  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      setLandscapeLinks(await get('/api/'));
      if (landscapeLinks) {
        setLandscapesCount(Object.keys(landscapeLinks._links).length);
        for (const landscapeLink in landscapeLinks._links) {
          const landscapeDescription: ILandscape | null = await get(
            landscapeLinks._links[landscapeLink].href
          );
          if (landscapeDescription) {
            setLandscapes((oldLandscapes) => [...oldLandscapes, landscapeDescription]);
          }
        }
      }
      setLoadLandscapes(false);
    }
  }, [loadLandscapes, landscapeLinks]);

  useEffect(() => {
    getLandscapes();
    setPageTitle(welcomeMessage);
  }, [getLandscapes, setPageTitle, welcomeMessage]);

  const loading = (
    <div className={classes.loading}>
      <div className={classes.loadingLogo}>
        <Avatar
          imgProps={{ style: { objectFit: 'contain' } }}
          src={withBasePath('icons/svg/nivio.svg')}
          className={classes.large}
        />
        <h2>Loading landscapes ...</h2>
      </div>
    </div>
  );

  return landscapes.length > 0 ? (
    landscapesCount > 1 ? (
      <OverviewLayout landscapes={landscapes} />
    ) : (
      <Redirect to={`/landscape/${landscapes[0]?.identifier}`} />
    )
  ) : (
    loading
  );
};

export default Overview;
