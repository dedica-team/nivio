import React, {Component} from 'react';
import PropTypes from 'prop-types';

class ItemModalContent extends Component {
    static propTypes = {
        element: PropTypes.object,
    };

    render() {
        const {element, closeFn} = this.props;

        let name = element.getAttribute("data-name") || element.getAttribute('data-identifier');
        let team = element.getAttribute("data-team") || '-';
        let owner = element.getAttribute("data-owner") || '-';
        let contact = element.getAttribute("data-contact") || '-';

        return <div>
            <div className={'control'}>
                <h2>{name}</h2>
            </div>
            <div className={'typewriter'}>
                Team: {team}<br/>
                Owner: {owner}<br/>
                Contact: {contact}<br/>
            </div>

            <button className={'control'} onClick={closeFn}>X</button>
        </div>
    }
}

export default ItemModalContent;
