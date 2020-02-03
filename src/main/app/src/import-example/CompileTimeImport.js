import React, {Component} from 'react';
import raw from "raw.macro";

const text = raw("./example.txt");

class CompileTimeImport extends Component {

    render() {
        return (<div>
            Imported text: {text}
        </div>);
    }
}

export default CompileTimeImport;