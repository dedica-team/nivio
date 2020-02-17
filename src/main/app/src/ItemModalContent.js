import React, {Component} from 'react';
import PropTypes from 'prop-types';
import $ from "jquery";

class ItemModalContent extends Component {
    static propTypes = {
        host: PropTypes.string,
        element: PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
        this.state = {
            html: null
        };
    }

    componentDidMount() {
        const {element} = this.props;
        const topic = element.getAttribute("data-identifier");
        if (this.state.html == null)
            fetch(this.props.host + "/docs/item/" + topic)
                .then((response) => {
                    return response.text()
                })
                .then((text) => {
                    let card = $(text).find(".card-body");
                    if (card.length > 0) {
                        this.setState({
                            html: card[0].outerHTML
                        });
                    } else {
                        this.setState({
                            html: '<h2>Not Found :(</h2>'
                        });
                    }
                });
    }

    render() {
        const {element, closeFn} = this.props;
        const topic = element.getAttribute("data-identifier");
        if (topic === undefined) {
            return <div><button className={'control'} onClick={closeFn}>X</button></div>
        }


        let html = this.state.html;
        return <div>
            <button className={'control'} onClick={closeFn} style={ {float: 'right'}}>OK</button>
            <div dangerouslySetInnerHTML={{__html: html + "<br /><br />"}}></div>

        </div>;
    }
}

export default ItemModalContent;
