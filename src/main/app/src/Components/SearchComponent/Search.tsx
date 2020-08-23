import React, {useEffect, useState} from 'react';
import './Search.scss';
import {TextField} from "@material-ui/core";
import {get} from "../../utils/API/APIClient";
import {IItem, Routes} from "../../interfaces";
import {withRouter, RouteComponentProps, matchPath} from 'react-router-dom';
import SearchResult from "./SearchResult";
import SearchIcon from '@material-ui/icons/Search';
import ClearIcon from '@material-ui/icons/Clear';
import {Clear} from "@material-ui/icons";
import IconButton from "@material-ui/core/IconButton";

interface PropsInterface extends RouteComponentProps {
}

const Search: React.FC<PropsInterface> = (props: PropsInterface) => {

    const match: { params?: { identifier?: string } } | null = matchPath(props.location.pathname, {
        path: Routes.MAP_ROUTE,
        exact: false,
        strict: false
    });

    const identifier = match?.params?.identifier;
    const [results, setResults] = useState<IItem[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [hasChange, setHasChange] = useState(false);

    async function search(searchTerm: string, identifier: string) {
        if (searchTerm.length < 2)
            return;

        const result: IItem[] | null = await get(
            '/api/landscape/' + identifier + '/search/' + encodeURIComponent(searchTerm)
                .replace(/[!'()]/g, escape)
                .replace(/\*/g, "%2A")
        );

        if (!result)
            return;

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
        if (identifier && hasChange)
            search(searchTerm, identifier)
    }, [identifier, searchTerm, results, hasChange]);

    if (identifier == null) {
        console.debug("identifier missing")
        return null;
    }


    // @ts-ignore
    const x = results.map(value1 => <SearchResult key={value1.fullyQualifiedIdentifier} item={value1}/>);
    return (
        <div className={'search'}>
            <SearchIcon className={'searchIcon'}></SearchIcon>
            <TextField value={searchTerm} onChange={event => setSearchTermSafely(event.target.value)}>Search</TextField>
            {results ? (<div className={'results'}>{x}</div>) : null}
            <IconButton className={'searchIcon'} size={"small"} onClick={e => clear()}>
                <Clear ></Clear>
            </IconButton>

        </div>
    );
};

export default withRouter(Search);