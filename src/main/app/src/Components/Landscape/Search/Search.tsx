import React, { useCallback, useEffect, useState } from 'react';

import {Box, Card, TextField, Theme} from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import { IItem, Routes } from '../../../interfaces';
import { withRouter, RouteComponentProps, matchPath } from 'react-router-dom';
import Item from '../Modals/Item/Item';
import SearchIcon from '@material-ui/icons/Search';
import { Backspace, ExpandMore } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    card: {
      margin: 5,
      padding: 5,
      backgroundColor: theme.palette.secondary.main,
    }
  })
);

interface PropsInterface extends RouteComponentProps {
  findItem?: Function;
  setSidebarContent: Function;
}

interface IFacet {
  dim: string;
  path: [];
  value: number;
  childCount: number;
  labelValues: ILabelValue[];
}

interface ILabelValue {
  label: string;
  value: number;
}

const Search: React.FC<PropsInterface> = (props: PropsInterface) => {
  const match: { params?: { identifier?: string } } | null = matchPath(props.location.pathname, {
    path: Routes.MAP_ROUTE,
    exact: false,
    strict: false,
  });

  const identifier = match?.params?.identifier;
  const [currentLandscape, setCurrentLandscape] = useState<string>('');
  const [results, setResults] = useState<IItem[]>([]);
  const [facets, setFacets] = useState<IFacet[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [hasChange, setHasChange] = useState(false);
  const classes = useStyles();
  const searchInput = React.useRef<HTMLDivElement>(null);

  const setSidebarContent = useCallback(
    (content: any) => {
      props.setSidebarContent(content);
    },
    [props]
  );

  const search = useCallback(
    (searchTerm: string, identifier: string) => {
      if (searchTerm.length < 2) return;

      get(
        '/api/landscape/' +
          identifier +
          '/search/' +
          encodeURIComponent(searchTerm)
            .replace(/[!'()]/g, escape)
            .replace(/\*/g, '%2A')
      ).then((result) => {
        setResults(result);
        const searchResult = results.map((value1) => (
          <Item
            small={true}
            key={value1.fullyQualifiedIdentifier}
            useItem={value1}
            findItem={props.findItem}
          />
        ));
        setSidebarContent(searchResult);
        setHasChange(false);
      });
    },
    [props, results, setSidebarContent]
  );

  async function loadFacets(identifier: string | undefined) {
    if (identifier == null) {
      console.debug('identifier missing');
      return;
    }

    const result: IFacet[] | null = await get('/api/landscape/' + identifier + '/facets/');

    if (!result) return;
    setFacets(result);
  }

  function setSearchTermSafely(newTerm: string) {
    if (newTerm !== searchTerm) {
      setSearchTerm(newTerm);
      setHasChange(true);
    }
  }

  function clear() {
    setSearchTerm('');
    setHasChange(false);
    setResults([]);
  }

  useEffect(() => {
    if (facets.length === 0) {
      loadFacets(identifier);
    }
  }, [identifier, facets]);

  useEffect(() => {
    if (identifier && hasChange) search(searchTerm, identifier);
  }, [identifier, searchTerm, results, hasChange, search]);

  if (identifier == null) {
    console.debug('identifier missing');
    return null;
  }

  if (currentLandscape == null || currentLandscape !== identifier) {
    setFacets([]);
    setCurrentLandscape(identifier);
  }

  const HtmlTooltip = withStyles((theme: Theme) => ({
    tooltip: {
      backgroundColor: '#f5f5f9',
      color: 'rgba(0, 0, 0, 0.87)',
      maxWidth: 220,
      fontSize: theme.typography.pxToRem(12),
      border: '1px solid #dadde9',
      textShadow: 'none',
    },
  }))(Tooltip);

  const facetsHtml = facets.map((facet) => (
    <Box className={classes.card} key={facet.dim}>
      <Typography variant={'h6'} >{facet.dim}</Typography>
      {facet.labelValues.map((lv) => (
        <Chip
          onClick={(e) => {
            let current = searchInput.current;
            if (!current) return;
            setSearchTermSafely(facet.dim + ':' + lv.label);
            current.focus();
          }}
          variant={'outlined'}
          size={"small"}
          key={facet.dim + '' + lv.label}
          label={lv.label}
          avatar={<Avatar>{lv.value}</Avatar>}
        />
      ))}
    </Box>
  ));

  return (
    <div className={'search'}>
      <HtmlTooltip
        title={
          <React.Fragment>
            <Typography color='inherit'>Search: </Typography>
            <strong>{'You can use the Lucene query syntax.'}</strong>
            <br />
            <em>{'foo*'}</em>
            <br />
            <em>{'*press'}</em>
            <br />
            <em>{'tag:cms'}</em>
            <br />
          </React.Fragment>
        }
      >
        <SearchIcon className={'searchIcon'} />
      </HtmlTooltip>
      <IconButton
        className={'searchIcon'}
        size={'small'}
        onClick={() => setSidebarContent(<Card className={classes.card}>{facetsHtml}</Card>)}
      >
        <ExpandMore></ExpandMore>
      </IconButton>
      <TextField
        value={searchTerm}
        onChange={(event) => setSearchTermSafely(event.target.value)}
        ref={searchInput}
      >
        Search
      </TextField>

      <IconButton className={'searchIcon'} size={'small'} onClick={() => clear()}>
        <Backspace></Backspace>
      </IconButton>
    </div>
  );
};

export default withRouter(Search);
