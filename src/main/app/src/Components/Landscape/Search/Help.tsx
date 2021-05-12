import React from 'react';

const SearchHelp: React.FC = () => {
  return (
    <div>
      <h3>Search</h3>
      Start typing to match items having name parts or description starting with the given term.
      Find everything starting with 'foo':
      <br />
      <pre>foo</pre>
      <br />
      Use whitespaces as AND condition (default). Search in name and description for both words:
      <br />
      <pre>MyService outdated</pre>
      <pre>MyService OR outdated</pre>
      <br />
      You can use the Lucene query syntax.
      <br />
      <pre>*foo</pre>
      <br />
      Apply facets (tags etc.) using a colon:
      <pre>'tag:cms'</pre>
    </div>
  );
};

export default SearchHelp;