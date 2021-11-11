import React, { useEffect, useState } from 'react';

import { Input, InputAdornment, Theme } from '@material-ui/core';
import { Backspace, SearchOutlined } from '@material-ui/icons';
import IconButton from '@material-ui/core/IconButton';
import { createStyles, makeStyles } from '@material-ui/core/styles';
import Search from './Search';

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    search: {
      margin: 0,
      marginLeft: 10,
      padding: 0,
      paddingLeft: 5,
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
    }
  })
);

interface Props {
  setSidebarContent: Function;
}

/**
 * Search input field displayed in the top nav
 *
 * @param setSidebarContent function the set content to the drawer sidebar
 */
const SearchField: React.FC<Props> = ({ setSidebarContent }) => {
  const classes = useStyles();
  const [searchTerm, setSearchTerm] = useState('');
  const [search, setSearch] = useState<any>(undefined);
  const searchInput = React.useRef<HTMLDivElement>(null);

  useEffect(() => {
    setSearch(<Search searchTerm={searchTerm} setSearchTerm={setSearchTerm} />);
  }, [searchTerm]);

  function clear() {
    setSearchTerm('');
  }

  return (
    <div className={classes.search}>
      <Input
        disableUnderline={true}
        className={classes.searchField}
        type={'text'}
        value={searchTerm}
        ref={searchInput}
        placeholder={'...'}
        onChange={(event) => setSearchTerm(event.target.value)}
        onFocus={() => setSidebarContent(search)}
        endAdornment={
          <InputAdornment position='end'>
            {searchTerm.length ? (
              <IconButton size={'small'} onClick={() => clear()} title={'Clear'}>
                <Backspace />
              </IconButton>
            ) : (
              <></>
            )}
            <IconButton
              size={'small'}
              onClick={() => setSidebarContent(search)}
              title={'Show results'}
            >
              <SearchOutlined />
            </IconButton>
          </InputAdornment>
        }
      />
    </div>
  );
};

export default SearchField;
