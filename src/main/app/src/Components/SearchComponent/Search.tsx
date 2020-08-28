import React, { useEffect, useState } from 'react';
import './Search.scss';
import { TextField, Theme } from '@material-ui/core';
import { get } from '../../utils/API/APIClient';
import { IItem, Routes } from '../../interfaces';
import { withRouter, RouteComponentProps, matchPath } from 'react-router-dom';
import SearchResult from './SearchResult';
import SearchIcon from '@material-ui/icons/Search';
import { Clear } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import withStyles from '@material-ui/core/styles/withStyles';
import Typography from '@material-ui/core/Typography';

interface PropsInterface extends RouteComponentProps {
  findItem?: (fullyQualifiedItemIdentifier: string) => void;
}

const Search: React.FC<PropsInterface> = (props: PropsInterface) => {
  const match: { params?: { identifier?: string } } | null = matchPath(props.location.pathname, {
    path: Routes.MAP_ROUTE,
    exact: false,
    strict: false,
  });

  const identifier = match?.params?.identifier;
  const [results, setResults] = useState<IItem[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [hasChange, setHasChange] = useState(false);

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
    },
  }))(Tooltip);

  const searchResult = results.map((value1) => (
    <SearchResult key={value1.fullyQualifiedIdentifier} item={value1} findItem={props.findItem} />
  ));
  return (
    <div className={'search'}>
      <HtmlTooltip
        title={
          <React.Fragment>
            <Typography color='inherit'>Search: </Typography>
            <em>{'foo*'}</em>
            <br />
            <em>{'*press'}</em>
            <br />
            <em>{'tag:cms'}</em>
            <br />
            <br />
            <strong>{'You can use the Lucene query syntax.'}</strong>
          </React.Fragment>
        }
      >
        <SearchIcon className={'searchIcon'} />
      </HtmlTooltip>
      <TextField value={searchTerm} onChange={(event) => setSearchTermSafely(event.target.value)}>
        Search
      </TextField>
      {results ? <div className={'results'}>{searchResult}</div> : null}

      <IconButton className={'searchIcon'} size={'small'} onClick={(e) => clear()}>
        <Clear></Clear>
      </IconButton>
    </div>
  );
};

export default withRouter(Search);
