import React from 'react';
import { render } from '@testing-library/react';
import Item from './Item';


it('should avoid displaying undefined and null value', () => {
    const IRelations = {
        source: "foo",
        target: "foo",
        description: "foo",
        format: "foo",
        name: "foo",
        id: "foo",
        direction: "foo",
    }
    const Irelations = { "foo": IRelations }
    const useItem = {
        identifier: "foo",
        group: "foo",
        name: "foo",
        owner: "foo",
        description: "foo",
        contact: "foo",
        relations: Irelations,
        interfaces: [],
        labels: { "foo": "foo" },
        type: "foo",
        fullyQualifiedIdentifier: "foo",
        tags: [],
        color: "foo",
        icon: "foo",

    }
    const { queryByText } = render(<Item useItem={useItem} fullyQualifiedItemIdentifier={"foo"} />);

    expect(queryByText('foo (undefined foo), format: foo')).toBeNull();
    expect(queryByText('undefined')).toBeNull();
    expect(queryByText('null')).toBeNull();


});