import React, {useEffect, useState} from 'react';
import './Search.scss';
import {TextField} from "@material-ui/core";
import {get} from "../../utils/API/APIClient";
import LandscapeItem from "../LandscapeComponent/LandscapeItem/LandscapeItem";
import {IItem, Routes} from "../../interfaces";
import {withRouter, RouteComponentProps, matchPath} from 'react-router-dom';

interface PropsInterface extends RouteComponentProps {
}

const Search: React.FC<PropsInterface> = (props: PropsInterface) => {

    const match: { params?: { identifier?: string } } | null = matchPath(props.location.pathname, {
        path: Routes.MAP_ROUTE,
        exact: false,
        strict: false
    });

    const identifier = match?.params?.identifier;
    const [results, setResults] = useState('');
    const [searchTerm, setSearchTerm] = useState('');
    const [hasChange, setHasChange] = useState(false);

    async function search(searchTerm: string, identifier: string) {
        if (searchTerm.length < 2)
            return;

        const result: IItem[] | null = await get(
            '/api/landscape/' + identifier + '/search/' + searchTerm
        );

        let map: any = result?.map(value1 => {
            return <LandscapeItem key={value1.fullyQualifiedIdentifier}
                                  fullyQualifiedItemIdentifier={value1.fullyQualifiedIdentifier} item={value1}
                                  small={true}
            />
        });
        setResults(map);
        setHasChange(false);
    }

    function setSearchTermSafely(newTerm: string) {
        if (newTerm !== searchTerm) {
            setSearchTerm(newTerm);
            setHasChange(true);
        }
    }

    useEffect(() => {
        if (identifier && hasChange)
            search(searchTerm, identifier)
    }, [identifier, searchTerm, results, hasChange]);

    if (identifier == null) {
        console.debug("identifier missing")
        return null;
    }


    return (
        <div className={'search'}>
            <TextField value={searchTerm} onChange={event => setSearchTermSafely(event.target.value)}>Search</TextField>
            { results ? (<div className={'results'}>{results}</div>) : ''}
        </div>
    );
};

export default withRouter(Search);
