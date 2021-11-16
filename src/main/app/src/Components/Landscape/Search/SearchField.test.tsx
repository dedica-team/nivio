import { getByDisplayValue, render } from "@testing-library/react";
import React from 'react';
import SearchField from "./SearchField";

describe('<SearchField />', () => {
  it('should render', () => {
    const { getByDisplayValue } = render(
        <SearchField setSidebarContent={() => {}} />
    );
    expect(getByDisplayValue('')).toBeInTheDocument();
  });
});
