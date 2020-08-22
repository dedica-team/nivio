import React, {useEffect, useState} from 'react';
import './Search.scss';
import {TextField} from "@material-ui/core";
import {get} from "../../utils/API/APIClient";
import LandscapeItem from "../LandscapeComponent/LandscapeItem/LandscapeItem";
import {IItem} from "../../interfaces";
import {withRouter, RouteComponentProps, matchPath} from 'react-router-dom';

interface PropsInterface extends RouteComponentProps {
}

const Search: React.FC<PropsInterface> = (props: PropsInterface) => {

    const match = matchPath(props.location.pathname, {
        path: "/landscape/:id",
        exact: true,
        strict: false
    });

    // @ts-ignore
    const identifier = match?.params?.id;

    const [results, setResults] = useState('');
    const [searchTerm, setSearchTerm] = useState('');

    async function search(searchTerm: string, identifier: string) {
        if (searchTerm.length < 2)
            return;

        const result: IItem[] | null = await get(
            '/api/landscape/' + identifier + '/search/' + searchTerm
        );

        let map: any = result?.map(value1 => {
            return <LandscapeItem key={value1.fullyQualifiedIdentifier}
                fullyQualifiedItemIdentifier={value1.fullyQualifiedIdentifier} item={value1} small={true}
            />
        });
        setResults(map);
    }

    function setSearchTermSafely(newTerm: string) {
        if (newTerm !== searchTerm)
            setSearchTerm(newTerm);
    }

    useEffect(() => {
        search(searchTerm, identifier)
    }, [identifier, searchTerm, results]);

    if (identifier == null) {
        console.debug("identifier missing")
        return null;
    }


    return (
        <div className={'search'}>
            <TextField value={searchTerm} onChange={event => setSearchTermSafely(event.target.value)}>Search</TextField>
            <div className={'results'}>{results}</div>
        </div>
    );
};

export default withRouter(Search);
