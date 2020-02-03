import React, {Component} from 'react';
import raw from "raw.macro";

const text = raw("./example.txt");

/**
 * This component show how to import files via raw loader.
 *
 * Can be removed later.
 *
 * @deprecated Only for demonstration.
 */
class CompileTimeImport extends Component {

    render() {
        return (<div>
            Imported text: {text}
        </div>);
    }
}

export default CompileTimeImport;