import React, { useCallback, useEffect, useState } from 'react';

import { Box, Card, Input, InputAdornment, Theme, Tooltip } from "@material-ui/core";
import { get } from '../../../utils/API/APIClient';
import { IItem, Routes } from '../../../interfaces';
import { withRouter, RouteComponentProps, matchPath } from 'react-router-dom';
import Item from '../Modals/Item/Item';
import { Backspace, SearchOutlined } from "@material-ui/icons";
import IconButton from '@material-ui/core/IconButton';
import Typography from '@material-ui/core/Typography';
import Chip from '@material-ui/core/Chip';
import Avatar from '@material-ui/core/Avatar';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import CardContent from '@material-ui/core/CardContent';
import componentStyles from '../../../Resources/styling/ComponentStyles';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    search: {
      margin: 0,
      padding: 0,
      borderRadius: 50,
      height: '2.5em',
      width: 310,
      marginRight: 2,
      paddingLeft: 10,
      border: '1px solid ',
      borderColor: theme.palette.primary.dark,
    },
    searchField: {
      marginTop: 2,
      width: 290
    }
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
    }).catch(reason => {
      console.warn(reason);
    });
  }, []);

  useEffect(() => {
    const searchResult = results.map((value1: IItem) => (
      <Item small={true} key={value1.fullyQualifiedIdentifier} useItem={value1} />
    ));
    setSidebarContent(searchResult);
  }, [results, setSidebarContent]);

  async function loadFacets(identifier: string | undefined) {
    if (identifier == null) {
      return;
    }

    const result: IFacet[] | null = await get('/api/landscape/' + identifier + '/facets/').catch(reason => console.warn(reason));

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

  const facetsHtml = facets.map((facet: IFacet) => (
    <Box key={facet.dim}>
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
    </Box>
  ));


  const searchSupport = <React.Fragment>
    <Card className={componentClasses.card}>
      <CardContent>
          {facetsHtml}
      </CardContent>
    </Card>

  </React.Fragment>;

  const help = <div>
    <strong>Hint: You can use the Lucene query syntax:</strong><br /><em>'foo*'</em><br /><em>'*press'</em><br /><em>'tag:cms'</em>
  </div>;

  return (
    <Box className={classes.search}>
      <Input
        disableUnderline={true}
        className={classes.searchField}
        type={'text'}
        value={searchTerm}
        ref={searchInput}
        placeholder={'Search'}
        onChange={(event) => setSearchTermSafely(event.target.value)}
        onFocus={event => setSidebarContent(searchSupport)}
        startAdornment={
          <Tooltip disableFocusListener title={help}>
            <SearchOutlined  />
          </Tooltip>
        }
        endAdornment={
          searchTerm.length ? <InputAdornment position="end">
             <IconButton
              size={'small'}
              onClick={() => clear()}
            >
              <Backspace />
            </IconButton>
          </InputAdornment> : null
        }
      />

    </Box>
  );
};

export default withRouter(Search);
