import React, { useContext, useEffect, useState } from 'react';

import { Box, Input, InputAdornment, Theme } from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import { IFacet, IItem } from '../../../interfaces';
import Item from '../Modals/Item/Item';
import { Backspace, Close, SearchOutlined } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import componentStyles from '../../../Resources/styling/ComponentStyles';
import HelpTooltip from '../../Help/HelpTooltip';
import Facets from './Facets';
import Typography from '@material-ui/core/Typography';
import SearchHelp from './Help';
import { withBasePath } from '../../../utils/API/BasePath';
import { SaveSearchConfig } from './SaveSearchConfig';
import { LandscapeContext } from '../../../Context/LandscapeContext';

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
      marginTop: 0,
      paddingLeft: 5,
      paddingRight: 5,
      width: '100%',
    },
  })
);

interface PropsInterface {
  setSidebarContent: Function;
  showSearch: Function;
}

const Search: React.FC<PropsInterface> = ({ setSidebarContent, showSearch }) => {
  const [currentLandscape, setCurrentLandscape] = useState<string>('');
  const [results, setResults] = useState<IItem[]>([]);
  const [facets, setFacets] = useState<IFacet[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchSupport, setSearchSupport] = useState<any>(null);
  const [render, setRender] = useState<boolean>(false);
  const classes = useStyles();
  const componentClasses = componentStyles();
  const searchInput = React.useRef<HTMLDivElement>(null);
  const landscapeContext = useContext(LandscapeContext);

  /**
   * Search on search term change, set results.
   */
  useEffect(() => {
    if (searchTerm.length < 2) return;

    get(
      '/api/landscape/' +
        landscapeContext.identifier +
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
  }, [searchTerm, landscapeContext.identifier]);

  /**
   * loading of facets
   *
   * also depends on assessments
   */
  useEffect(() => {
    const addFacet = (dim: string, label: string): string => {
      let current = searchInput.current;
      if (current && dim.length && label.length) {
        if (searchTerm.indexOf(dim + ':' + label) === -1) {
          if (label.indexOf(' ') !== -1) {
            label = `"${label}"`; //to handle whitespace
          }
          setSearchTerm(`${searchTerm} ${dim}:${label}`);
          setRender(true);
        }
        current.focus();
      }

      return searchTerm;
    };

    const saveSearch = (config: SaveSearchConfig): void => {
      if (!currentLandscape) return;

      const urlSearchParams = new URLSearchParams();
      urlSearchParams.set('searchTerm', searchTerm);
      if (config.title) {
        urlSearchParams.set('title', config.title);
      }
      const reportUrl = withBasePath(
        '/docs/' + currentLandscape + '/owners.html?' + urlSearchParams.toString()
      );
      window.open(reportUrl, '_blank');
    };

    setSearchSupport(<Facets facets={facets} addFacet={addFacet} saveSearch={saveSearch} />);
  }, [setSearchSupport, searchTerm, facets, componentClasses.card, currentLandscape]);

  /**
   * Update rendered search results
   */
  useEffect(() => {
    const searchResult = results.map((value1: IItem) => (
      <Item
        small={true}
        key={`item_${value1.fullyQualifiedIdentifier}_${Math.random()}`}
        fullyQualifiedItemIdentifier={value1.fullyQualifiedIdentifier}
      />
    ));
    setSidebarContent(<>{searchResult}</>);
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
    if (landscapeContext.identifier) loadFacets(landscapeContext.identifier);
  }, [landscapeContext.identifier, landscapeContext.assessment]);

  if (landscapeContext.identifier == null) {
    return null;
  }

  if (currentLandscape == null || currentLandscape !== landscapeContext.identifier) {
    setFacets([]);
    setSearchTerm('');
    setCurrentLandscape(landscapeContext.identifier);
  }

  return (
    <div>
      <div style={{ float: 'right', padding: 2 }}>
        <IconButton size={'small'}>
          <HelpTooltip style={{ float: 'right', padding: 2 }} content={<SearchHelp />} />
        </IconButton>
        <IconButton size={'small'} onClick={() => showSearch(false)} title={'Close search'}>
          <Close />
        </IconButton>
      </div>
      <Typography variant={'h5'}>Search</Typography>
      <Box className={classes.search}>
        <Input
          disableUnderline={true}
          className={classes.searchField}
          type={'text'}
          value={searchTerm}
          ref={searchInput}
          placeholder={'...'}
          onChange={(event) => setSearchTerm(event.target.value)}
          startAdornment={
            <IconButton size={'small'} onClick={() => setRender(!render)} title={'Show results'}>
              <SearchOutlined />
            </IconButton>
          }
          endAdornment={
            searchTerm.length ? (
              <InputAdornment position='end'>
                <IconButton size={'small'} onClick={() => clear()} title={'Clear'}>
                  <Backspace />
                </IconButton>
              </InputAdornment>
            ) : null
          }
        />
        {searchSupport}
      </Box>
    </div>
  );
};

export default Search;
