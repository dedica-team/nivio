import React, { useContext, useEffect, useState } from 'react';

import { Theme } from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import { IFacet, IItem } from '../../../interfaces';
import Item from '../Modals/Item/Item';
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
    searchContainer: {
      display: 'flex',
      flexDirection: 'column',
      height: '100%',
    },
    search: {
      margin: 0,
      marginBottom: '1em',
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
    searchResults: {
      marginTop: '1em',
      flexGrow: 1,
      flexShrink: 1,
      overflowY: 'auto',
    },
  })
);

interface SearchProps {
  searchTerm: string;
  setSearchTerm: Function;
}

const Search: React.FC<SearchProps> = ({setSearchTerm, searchTerm}) => {
  const [currentLandscape, setCurrentLandscape] = useState<string>('');
  const [results, setResults] = useState<IItem[]>([]);
  const [renderedResults, setRenderedResults] = useState<any>([]);
  const [facets, setFacets] = useState<IFacet[]>([]);
  const [searchSupport, setSearchSupport] = useState<any>(null);
  const [render, setRender] = useState<boolean>(false);
  const classes = useStyles();
  const componentClasses = componentStyles();
  const landscapeContext = useContext(LandscapeContext);

  /**
   * Search on search term change, set results.
   */
  useEffect(() => {
    if (searchTerm.length < 2) {
      setResults([]);
      return;
    }

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
  }, [searchTerm, landscapeContext.identifier, render]);

  useEffect(() => {
    if (results && results.length > 0) {
      setRenderedResults(
        results.map((value1: IItem) => (
          <Item
            small={true}
            closable={false}
            key={`item_${value1.fullyQualifiedIdentifier}_${Math.random()}`}
            fullyQualifiedItemIdentifier={value1.fullyQualifiedIdentifier}
          />
        ))
      );
      return;
    }

    let msg = '...';
    if (searchTerm && searchTerm.length) {
      msg = 'No results found.';
    }
    setRenderedResults(<>{msg}</>);
  }, [results, searchTerm]);

  /**
   * loading of facets
   *
   * also depends on assessments
   */
  useEffect(() => {
    const addFacet = (dim: string, label: string): string => {
        if (searchTerm.indexOf(dim + ':' + label) === -1) {
          if (label.indexOf(' ') !== -1) {
            label = `"${label}"`; //to handle whitespace
          }
          setSearchTerm(`${searchTerm} ${dim}:${label}`);
          setRender(true);
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
  }, [setSearchSupport, searchTerm, setSearchTerm, facets, componentClasses.card, currentLandscape]);

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
    <div className={classes.searchContainer}>
      <div>
        <Typography variant={'h5'}>
          Search
          <IconButton size={'small'}>
            <HelpTooltip style={{ float: 'right', padding: 2 }} content={<SearchHelp />} />
          </IconButton>
        </Typography>
      </div>

      {searchSupport}
      <div className={classes.searchResults}>
        <Typography variant={'h5'}>Results for '{searchTerm}'</Typography>
        {renderedResults}
      </div>
    </div>
  );
};

export default Search;
