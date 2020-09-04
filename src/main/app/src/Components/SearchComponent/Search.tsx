import React, { useEffect, useState } from 'react';
import './Search.scss';
import { TextField, Theme } from '@material-ui/core';
import { get } from '../../utils/API/APIClient';
import { IItem, Routes } from '../../interfaces';
import { withRouter, RouteComponentProps, matchPath } from 'react-router-dom';
import SearchResult from './SearchResult';
import SearchIcon from '@material-ui/icons/Search';
import { Backspace } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';

interface PropsInterface extends RouteComponentProps {
  findItem?: (fullyQualifiedItemIdentifier: string) => void;
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
  const [results, setResults] = useState<IItem[]>([]);
  const [facets, setFacets] = useState<IFacet[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [hasChange, setHasChange] = useState(false);
  const [hasFocus, setHasFocus] = useState(false);

  const searchInput = React.useRef<HTMLDivElement>(null);

  async function search(searchTerm: string, identifier: string) {
    if (searchTerm.length < 2) return;

    const result: IItem[] | null = await get(
      '/api/landscape/' +
        identifier +
        '/search/' +
        encodeURIComponent(searchTerm)
          .replace(/[!'()]/g, escape)
          .replace(/\*/g, '%2A')
    );

    if (!result) return;

    setResults(result);
    setHasChange(false);
  }

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
      setHasFocus(false);
    }
  }

  function clear() {
    setSearchTerm('');
    setHasChange(false);
    setHasFocus(false);
    setResults([]);
  }

  useEffect(() => {
    if (facets.length === 0) {
      loadFacets(identifier);
    }
  }, [identifier, facets]);

  useEffect(() => {
    if (identifier && hasChange) search(searchTerm, identifier);
  }, [identifier, searchTerm, results, hasChange]);

  if (identifier == null) {
    console.debug('identifier missing');
    return null;
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

  const searchResult = results.map((value1) => (
    <SearchResult key={value1.fullyQualifiedIdentifier} item={value1} findItem={props.findItem} />
  ));
  const facetsHtml = facets.map((facet) => (
    <div className={'facet'} key={facet.dim}>
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
          key={facet.dim + '' + lv.label}
          label={lv.label}
          avatar={<Avatar>{lv.value}</Avatar>}
        />
      ))}
      <br />
    </div>
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
      <TextField
        value={searchTerm}
        onChange={(event) => setSearchTermSafely(event.target.value)}
        onFocus={() => setHasFocus(true)}
        ref={searchInput}
      >
        Search
      </TextField>
      {results ? <div className={'results'}>{searchResult}</div> : null}

      <IconButton className={'searchIcon'} size={'small'} onClick={() => clear()}>
        <Backspace></Backspace>
      </IconButton>
      {hasFocus && !searchTerm ? <div className={'facets'}>{facetsHtml}</div> : null}
    </div>
  );
};

export default withRouter(Search);
