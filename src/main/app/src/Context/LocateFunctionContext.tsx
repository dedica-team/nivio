import React, { useState } from 'react';

const initialLocateFunction: (fqi: string) => void = () => {};

export interface LocateFunctionContextType {
  locateFunction: (fqi: string) => void;
  setLocateFunction: (locateFunction: () => void) => void;
}

export const LocateFunctionContext = React.createContext<LocateFunctionContextType>({
  locateFunction: initialLocateFunction,
  setLocateFunction: () => {},
});

/**
 * Visit https://reactjs.org/docs/context.html to read about how to implement and use the context in React.
 */
const LocateFunctionContextProvider: React.FC<{}> = (props) => {
  const [locateFunction, setLocateFunction] = useState<(fqi: string) => void>(
    initialLocateFunction
  );

  return (
    <LocateFunctionContext.Provider
      value={{
        locateFunction: locateFunction,
        setLocateFunction: setLocateFunction,
      }}
    >
      {props.children}
    </LocateFunctionContext.Provider>
  );
};

export { LocateFunctionContextProvider };
