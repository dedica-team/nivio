import React, {Component} from 'react';
import PropTypes from 'prop-types';

class LandscapeLog extends Component {
    static propTypes = {
        baseUrl: PropTypes.string,
        landscape: PropTypes.object
    };

    constructor(props, context) {
        super(props, context);
        this.state = {
            data: null
        };
    }

    componentDidMount() {
        const {baseUrl, landscape} = this.props;
        if (this.state.data == null)
            fetch(baseUrl + "/api/landscape/" + landscape.identifier + "/log")
                .then((response) => {
                    return response.json()
                })
                .then((json) => {
                        this.setState({
                            data: json
                        });
                });
    }

    render() {
        const {landscape, closeFn} = this.props;
        const data = this.state.data;
        let content;
        if (!data) {
            content = "loading...";
        } else {
            content = data.messages.map(m => {
                return <div>{m}</div>
            });
        }

        return (
            <div>
                <h1>Landscape {landscape.name} Process Log</h1>
                {content}
                <br/>
                <button className={'control'} onClick={closeFn}>close</button>
            </div>
        );
    }
}

export default LandscapeLog;
