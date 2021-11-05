import {IItem} from '../../../interfaces';
import {render} from '@testing-library/react';
import React from 'react';
import {getLabels, getLabelsWithPrefix, getMappedLabels} from './utils';
import {getByText} from "@testing-library/dom/types/queries";
import {FrontendMappingContext} from "../../../Context/FrontendMappingContext";
import frontendMappingContextType from "../../../utils/testing/FrontendMappingContextType";

const item: IItem = {
    contact: 'marvin',
    group: 'test',
    owner: 'daniel',
    fullyQualifiedIdentifier: 'fullTestIdentifier',
    identifier: 'testIdentifier',
    name: 'testIdentifier',
    description: 'testDescription',
    relations: {},
    labels: {
        'foo': 'bar',
        'color': 'ffeecc',
        'icon': 'hiu',
        'framework.java': '8',
        'framework.react': '84711',
        'shortname': 'END_OF_LIFE',
    },
    tags: [],
    type: 'service',
    icon: '',
};

describe('getLabels', () => {
    it('should render labels', () => {
        const {getByText} = render(<>{getLabels(item)}</>);
        expect(getByText('foo')).toBeInTheDocument();
    });
    it('should not render hidden labels', () => {
        const {queryByText} = render(<>{getLabels(item)}</>);
        expect(queryByText('java')).not.toBeInTheDocument();
        expect(queryByText('icon')).not.toBeInTheDocument();
        expect(queryByText('color')).not.toBeInTheDocument();
    });
});

describe('getLabelsWithPrefix', () => {
    it('should render prefixed labels', () => {
        const {getByText} = render(<>{getLabelsWithPrefix('framework', item)}</>);
        expect(getByText('java')).toBeInTheDocument();
    });
    it('should not render other labels', () => {
        const {queryByText} = render(<>{getLabelsWithPrefix('framework', item)}</>);
        expect(queryByText('foo')).not.toBeInTheDocument();
        expect(queryByText('icon')).not.toBeInTheDocument();
        expect(queryByText('color')).not.toBeInTheDocument();
    });
});

describe('frontendMapping', () => {
    it('should map the labels from the mapping API', () => {
        const {getByText} = render(<FrontendMappingContext.Provider
            value={frontendMappingContextType}>{getMappedLabels(item)}</FrontendMappingContext.Provider>);
        expect(getByText('short name')).toBeInTheDocument();
        expect(getByText('end of life')).toBeInTheDocument();
    });
});
