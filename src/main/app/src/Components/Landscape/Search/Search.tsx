import React, { useEffect, useState } from 'react';

import { Box, Card, Input, InputAdornment, Theme } from '@material-ui/core';
import { get } from '../../../utils/API/APIClient';
import { IItem, Routes } from '../../../interfaces';
import { matchPath, RouteComponentProps, withRouter } from 'react-router-dom';
import Item from '../Modals/Item/Item';
import { Backspace, SearchOutlined } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../../Resources/styling/ComponentStyles';
import HelpTooltip from '../../Help/HelpTooltip';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    search: {
      margin: 0,
      padding: 0,
      borderRadius: 50,
      height: '2.5em',
      width: 266,
      marginRight: 2,
      paddingLeft: 10,
      border: '1px solid ',
      borderColor: theme.palette.primary.dark,
    },
    searchField: {
      marginTop: 2,
      width: 250,
    },
  })
);

interface PropsInterface extends RouteComponentProps {
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

const Search: React.FC<PropsInterface> = ({ setSidebarContent, ...props }) => {
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
    const facetsHtml = facets.map((facet: IFacet) => (
      <Box key={facet.dim}>
        <Typography variant={'h6'}>{facet.dim}</Typography>
        {facet.labelValues.map((lv) => (
          <Chip
            onClick={(e) => {
              let current = searchInput.current;
              if (!current) return;
              if (searchTerm.indexOf(facet.dim + ':' + lv.label) === -1) {
                setSearchTerm(searchTerm + ' ' + facet.dim + ':' + lv.label);
                setRender(true);
              }
              current.focus();
            }}
            variant={'outlined'}
            size={'small'}
            key={facet.dim + '' + lv.label}
            label={lv.label}
            avatar={<Avatar>{lv.value}</Avatar>}
          />
        ))}
      </Box>
    ));

    const help = (
      <div>
        <h3>Search</h3>
        Start typing to match items having name parts or description starting with the given term.
        Find everything starting with 'foo':
        <br />
        <pre>foo</pre>
        <br />
        Use whitespaces as AND condition (default). Search in name and description for both words:
        <br />
        <pre>MyService outdated</pre>
        <pre>MyService OR outdated</pre>
        <br />
        You can use the Lucene query syntax.
        <br />
        <pre>*foo</pre>
        <br />
        Apply facets (tags etc.) using a colon:
        <pre>'tag:cms'</pre>
      </div>
    );

    setSearchSupport(
      <Card className={componentClasses.card}>
        <HelpTooltip style={{ float: 'right', padding: 2, color: '#222'}} content={help} />
        <CardContent>{facetsHtml}</CardContent>
      </Card>
    );
  }, [setSearchSupport, searchTerm, facets, componentClasses.card]);

  /**
   * Update rendered search results
   */
  useEffect(() => {
    const searchResult = results.map((value1: IItem) => (
      <Item small={true} key={value1.fullyQualifiedIdentifier} useItem={value1} />
    ));
    if (render)
      setSidebarContent(
        <>
          {searchSupport}
          {searchResult}
        </>
      );
  }, [results, setSidebarContent, searchSupport, render]);

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
    console.debug('identifier missing');
    return null;
  }

  if (currentLandscape == null || currentLandscape !== identifier) {
    setFacets([]);
    setSearchTerm('');
    setCurrentLandscape(identifier);
  }

  return (
    <Box className={classes.search}>
      <Input
        disableUnderline={true}
        className={classes.searchField}
        type={'text'}
        value={searchTerm}
        ref={searchInput}
        placeholder={'Search'}
        onChange={(event) => setSearchTerm(event.target.value)}
        onFocus={(event) => {
          setRender(true);
        }}
        onBlur={(event) => {
          setRender(false);
        }}
        startAdornment={<SearchOutlined />}
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
    </Box>
  );
};

export default withRouter(Search);
