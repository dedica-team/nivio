import React from 'react';
import { render } from '@testing-library/react';
import StatusChip from './StatusChip';


it('should avoid displaying undefined and null value in status', () => {

    const { queryByText, getByText } = render(<StatusChip name={"foo"} status={"foo"} />);
    expect(queryByText('null')).toBeNull();
    expect(queryByText('undefined')).toBeNull();
    expect(queryByText('foo')).toBeInTheDocument();


});