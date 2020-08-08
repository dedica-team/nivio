import React from 'react';
import './Search.scss';
import {TextField} from "@material-ui/core";
import {get} from "../../utils/API/APIClient";

const Search: React.FC = () => {


    async function search(event: React.ChangeEvent<HTMLTextAreaElement | HTMLInputElement>) {
        let value = event.target.value;
        if (value.length < 2)
            return;

        const result: [] | null = await get(
            `/api/landscape/nivio:example/search/` + value
        );
        console.log(result);
    }

    return (
        <form>
            <TextField onChange={event => search(event)}>Search</TextField>
        </form>
    );
};

export default Search;
