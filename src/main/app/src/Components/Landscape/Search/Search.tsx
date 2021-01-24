import React, { useCallback, useEffect, useState } from 'react';

import { Card, CardHeader, TextField, Theme } from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import { IItem, Routes } from '../../../interfaces';
import { withRouter, RouteComponentProps, matchPath } from 'react-router-dom';
import Item from '../Modals/Item/Item';
import { Backspace, MoreVertSharp } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../../Ressources/styling/ComponentStyles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    searchField: {
      margin: 0,
      padding: 0,
      borderRadius: 5,
      backgroundColor: theme.palette.primary.dark,
    },
  })
);

interface PropsInterface extends RouteComponentProps {
  locateFunction: Function;
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

const Search: React.FC<PropsInterface> = ({ locateFunction, setSidebarContent, ...props }) => {
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
  const componentClasses = componentStyles();
  const searchInput = React.useRef<HTMLDivElement>(null);

  const search = useCallback((searchTerm: string, identifier: string) => {
    if (searchTerm.length < 2) return;
    setHasChange(false);
    get(
      '/api/landscape/' +
        identifier +
        '/search/' +
        encodeURIComponent(searchTerm)
          .replace(/[!'()]/g, escape)
          .replace(/\*/g, '%2A')
    ).then((result) => {
      setResults(result);
    });
  }, []);

  useEffect(() => {
    const searchResult = results.map((value1) => (
      <Item
        small={true}
        key={value1.fullyQualifiedIdentifier}
        useItem={value1}
        locateItem={locateFunction}
      />
    ));
    setSidebarContent(searchResult);
  }, [results, setSidebarContent]);// eslint-disable-line react-hooks/exhaustive-deps

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
    setSidebarContent(null);
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

  const facetsHtml = facets.map((facet) => (
    <Card className={componentClasses.card} key={facet.dim}>
      <CardContent>
        <Typography variant={'h6'}>{facet.dim}</Typography>
        {facet.labelValues.map((lv) => (
          <Chip
            onClick={(e) => {
              let current = searchInput.current;
              if (!current) return;
              setSearchTermSafely(facet.dim + ':' + lv.label);
              current.focus();
            }}
            variant={'outlined'}
            size={'small'}
            key={facet.dim + '' + lv.label}
            label={lv.label}
            avatar={<Avatar>{lv.value}</Avatar>}
          />
        ))}
      </CardContent>
    </Card>
  ));

  return (
    <React.Fragment>
      <IconButton
        size={'small'}
        color={'secondary'}
        onClick={() =>
          setSidebarContent(
            <React.Fragment>
              <Card className={componentClasses.card}>
                <CardHeader title={'Search'} className={componentClasses.cardHeader} />
                <CardContent>
                  <strong>{'You can use the Lucene query syntax.'}</strong>
                  <br />
                  <em>{'foo*'}</em>
                  <br />
                  <em>{'*press'}</em>
                  <br />
                  <em>{'tag:cms'}</em>
                </CardContent>
              </Card>
              {facetsHtml}
            </React.Fragment>
          )
        }
      >
        <MoreVertSharp />
      </IconButton>
      <TextField
        className={classes.searchField}
        value={searchTerm}
        onChange={(event) => setSearchTermSafely(event.target.value)}
        ref={searchInput}
        variant={'outlined'}
        margin={'dense'}
        placeholder={'Search'}
      />

      <IconButton
        className={'searchIcon'}
        size={'small'}
        onClick={() => clear()}
        color={'secondary'}
      >
        <Backspace />
      </IconButton>
    </React.Fragment>
  );
};

export default withRouter(Search);
