import React from "react";
import { render } from "@testing-library/react";
import { FrontendMappingContext } from "../../../Context/FrontendMappingContext";
import frontendMappingContextType from "../../../utils/testing/FrontendMappingContextType";
import MappedString from "./MappedString";

describe('<MappedString />', () => {

  it('should display', () => {
    const { getByText } = render(
      <FrontendMappingContext.Provider value={frontendMappingContextType}>
        <MappedString mapKey={"shortname"} />
      </FrontendMappingContext.Provider>
    );
    expect(getByText('short name')).toBeInTheDocument();
  });

  it('should fall back', () => {
    const { getByText } = render(
      <FrontendMappingContext.Provider value={frontendMappingContextType}>
        <MappedString mapKey={"foo"} />
      </FrontendMappingContext.Provider>
    );
    expect(getByText('foo')).toBeInTheDocument();
  });

});
