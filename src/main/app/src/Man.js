import React, {Component} from 'react';
import PropTypes from 'prop-types';
import $ from "jquery";

class Man extends Component {

    static propTypes = {
        host: PropTypes.string,
        topic: PropTypes.string,
    };

    constructor(props, context) {
        super(props, context);
        this.state = {
            html: null
        };
    }

    render() {
        let {topic, closeFn} = this.props;
        if (topic === undefined)
            topic = 'install';

        if (this.state.html == null)
            fetch(this.props.host + "/docs/" + topic + ".html")
                .then((response) => {
                    return response.text()
                })
                .then((text) => {

                    this.setState({
                        html: $(text).find(".body")[0].outerHTML
                    })
                });

        let html = this.state.html;

        return <div>
            <button className={'control'} onClick={closeFn} style={ {float: 'right'}}>close</button>
            <div dangerouslySetInnerHTML={{__html: html + "<br /><br />"}}></div>

        </div>;

    }
}

export default Man;