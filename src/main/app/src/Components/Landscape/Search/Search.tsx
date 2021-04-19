import React, { useEffect, useState } from 'react';

import { Box, Input, InputAdornment, Theme } from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import { IItem, Routes } from '../../../interfaces';
import { matchPath, RouteComponentProps, withRouter } from 'react-router-dom';
import Item from '../Modals/Item/Item';
import { Backspace, Close, SearchOutlined } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import componentStyles from '../../../Resources/styling/ComponentStyles';
import HelpTooltip from '../../Help/HelpTooltip';
import Facets from './Facets';
import Typography from '@material-ui/core/Typography';
import SearchHelp from './Help';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    search: {
      margin: 0,
      padding: 0,
      borderRadius: 50,
      height: '2.5em',
      border: '1px solid ',
      backgroundColor: theme.palette.background.default,
      borderColor: theme.palette.primary.main,
    },
    searchField: {
      marginTop: 2,
      paddingRight: 5,
      width: '100%',
    },
  })
);

interface PropsInterface extends RouteComponentProps {
  setSidebarContent: Function;
  showSearch: Function;
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

const Search: React.FC<PropsInterface> = ({ setSidebarContent, showSearch, ...props }) => {
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
  const [searchSupport, setSearchSupport] = useState<any>(null);
  const [render, setRender] = useState<boolean>(false);
  const classes = useStyles();
  const componentClasses = componentStyles();
  const searchInput = React.useRef<HTMLDivElement>(null);

  /**
   * Search on search term change, set results.
   */
  useEffect(() => {
    if (searchTerm.length < 2) return;

    get(
      '/api/landscape/' +
        identifier +
        '/search/' +
        encodeURIComponent(searchTerm)
          .replace(/[!'()]/g, escape)
          .replace(/\*/g, '%2A')
    )
      .then((result) => {
        setResults(result);
      })
      .catch((reason) => {
        console.warn(reason);
      });
  }, [searchTerm, identifier]);

  /**
   * Initial loading of facets
   */
  useEffect(() => {
    const addFacet = (dim: string, label: string) => {
      let current = searchInput.current;
      if (!current) return;
      if (searchTerm.indexOf(dim + ':' + label) === -1) {
        setSearchTerm(searchTerm + ' ' + dim + ':' + label);
        setRender(true);
      }
      current.focus();
    };

    setSearchSupport(<Facets facets={facets} addFacet={addFacet} />);
  }, [setSearchSupport, searchTerm, facets, componentClasses.card]);

  /**
   * Update rendered search results
   */
  useEffect(() => {
    const searchResult = results.map((value1: IItem) => (
      <Item small={true} key={value1.fullyQualifiedIdentifier} useItem={value1} />
    ));
    if (render) setSidebarContent(<>{searchResult}</>);
  }, [results, setSidebarContent, render]);

  async function loadFacets(identifier: string | undefined) {
    if (identifier == null) {
      return;
    }
    const result: IFacet[] | null = await get(
      '/api/landscape/' + identifier + '/facets/'
    ).catch((reason) => console.warn(reason));

    if (!result) return;
    setFacets(result);
  }

  function clear() {
    setSearchTerm('');
    setResults([]);
    setSidebarContent(null);
  }

  useEffect(() => {
    if (facets.length === 0) {
      loadFacets(identifier);
    }
  }, [identifier, facets]);

  if (identifier == null) {
    return null;
  }

  if (currentLandscape == null || currentLandscape !== identifier) {
    setFacets([]);
    setSearchTerm('');
    setCurrentLandscape(identifier);
  }

  return (
    <div>
      <div style={{ float: 'right', padding: 2, color: '#222' }}>
        <IconButton size={'small'}>
          <HelpTooltip
            style={{ float: 'right', padding: 2, color: '#222' }}
            content={<SearchHelp />}
          />
        </IconButton>
        <IconButton size={'small'} onClick={() => showSearch(false)} title={'Close search'}>
          <Close />
        </IconButton>
      </div>
      <Typography variant={'h4'}>Search</Typography>
      <Box className={classes.search}>
        <Input
          disableUnderline={true}
          className={classes.searchField}
          type={'text'}
          value={searchTerm}
          ref={searchInput}
          placeholder={'...'}
          onChange={(event) => setSearchTerm(event.target.value)}
          onFocus={(event) => {
            setRender(true);
          }}
          onBlur={(event) => {
            setRender(false);
          }}
          startAdornment={
            <IconButton size={'small'} onClick={() => setSearchTerm(searchTerm + '')}>
              <SearchOutlined />
            </IconButton>
          }
          endAdornment={
            searchTerm.length ? (
              <InputAdornment position='end'>
                <IconButton size={'small'} onClick={() => clear()}>
                  <Backspace />
                </IconButton>
              </InputAdornment>
            ) : null
          }
        />
        <Typography variant={'h6'}>Filters</Typography>
        {searchSupport}
      </Box>
    </div>
  );
};

export default withRouter(Search);
