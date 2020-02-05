import React, {Component} from 'react';
import PropTypes from 'prop-types';
import $ from "jquery";
import raw from "raw.macro";

const topics = {
    install: raw('../../../../docs/build/install.html'),
    features: raw('../../../../docs/build/features.html'),
    input: raw('../../../../docs/build/input.html'),
    extra: raw('../../../../docs/build/extra.html'),
    api: raw('../../../../docs/build/api.html'),
    magic: raw('../../../../docs/build/magic.html'),
    model: raw('../../../../docs/build/model.html')
};

class Man extends Component {

    static propTypes = {
        host: PropTypes.string,
        topic: PropTypes.string,
    };

    static USAGE = 'man install|input|model|magic|extra|api';

    constructor(props, context) {
        super(props, context);
        this.state = {
            html: null
        };
    }

    render() {
        let {topic, closeFn} = this.props;
        if (topic === undefined || topics[topic] === undefined) {
            topic = 'install';
        }
        if (this.state.html === null) {
            let body = topics[topic];
            this.setState({
                html: $(body + '').find(".body")[0].outerHTML
            });
        }


        let html = this.state.html;

        return <div>
            <button className={'control'} onClick={closeFn} style={{float: 'right'}}>close</button>
            <div dangerouslySetInnerHTML={{__html: html + "<br /><br />"}}></div>

        </div>;

    }
}

export default Man;