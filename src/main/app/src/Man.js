import React, {Component} from 'react';
import PropTypes from 'prop-types';
import $ from "jquery";
import ReactHtmlParser from 'react-html-parser';


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
        let topic = this.props.topic;
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

        return <div dangerouslySetInnerHTML={{__html: html + "<br /><br />"}}></div>;

    }
}

export default Man;