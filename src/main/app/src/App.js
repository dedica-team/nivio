import React, {Component} from 'react';
import {INITIAL_VALUE, ReactSVGPanZoom, TOOL_AUTO} from 'react-svg-pan-zoom';
import {ReactSvgPanZoomLoader, SvgLoaderSelectElement} from 'react-svg-pan-zoom-loader'
import {BrowserRouter as Router, Redirect, Switch, Route, Link} from "react-router-dom";
import Terminal from 'react-console-emulator'
import Modal from 'react-modal';
import ItemModalContent from "./ItemModalContent";
import Man from "./Man";

class App extends Component {

    Viewer = null;

    constructor(props, context) {
        super(props, context);
        this.state = {
            landscapes: null,
            landscape: null,
            tool: TOOL_AUTO,
            value: INITIAL_VALUE,
            newLocation: null,
            message: null,
            modalContent: null
        };

        this.host = window.location.protocol + "//" + window.location.host + "/" + window.location.pathname;
        if (!process.env.NODE_ENV || process.env.NODE_ENV === 'development') {
            this.host = 'http://localhost:8081';
        }
        let params = new URLSearchParams(window.location.search);
        let host = params.get('host');
        if (host !== null) {
            this.host = host;
        }

        this.onItemClick = this.onItemClick.bind(this);
        this.onModalClose = this.onModalClose.bind(this);
        Modal.setAppElement('#root')

    }

    componentDidMount() {
        this.getLandscapes();
    }

    changeValue(nextValue) {
        this.setState({value: nextValue})
    }

    getLandscapes() {
        fetch(this.host + "/api/")
            .then((response) => {
                return response.json()
            })
            .then((json) => {
                this.setState({
                    landscapes: json,
                    message: 'Loaded landscapes.'
                })
            });
    }

    getMapData(landscape) {
        let params = new URLSearchParams(window.location.search);
        let data = params.get('data');
        if (data === undefined) {
            alert("data param missing");
            return;
        }

        fetch(data)
            .then((response) => {
                return response.json()
            })
            .then((json) => {
                this.setState({
                    mapData: json,
                    message: 'Loaded data.'
                })
            });
    }

    enterLandscape(l) {
        this.setState({message: 'Entering landscape: ' + l.identifier, landscape: l});
    }

    render() {

        if (this.state.newLocation !== null) {
            this.setState({newLocation: null});
            return <Redirect to={this.state.newLocation}/>
        }

        let message = this.state.message;
        return <Router>
            <Switch>
                <Route exact path="/" render={() => this.Home()}></Route>
                <Route path="/landscape" render={() => this.Landscape()}></Route>
                <Route path="/man" render={() => this.Manual()}></Route>
            </Switch>
            <footer key={'footer'} id={'footer'} style={{position: 'fixed', bottom: 0, width: '100%'}}>
                <div className={'typewriter'}>{message}</div>
                <Terminal commands={this.commands()} promptLabel={'>'} autoFocus={true}
                          style={{width: '100%', minHeight: null}} className={'console'}
                          contentStyle={{padding: '0.5em'}}
                          inputAreaStyle={{height: '1em'}}/>
            </footer>
        </Router>
    }

    commands() {
        let that = this;
        return {
            cd: {
                description: 'Back to the start.',
                usage: 'cd',
                fn: () => that.setState({newLocation: "/"})
            },
            man: {
                description: 'Show the manual.',
                usage: 'man install|features|model|extra|api',
                fn: (arg) => that.setState({message: 'RTFM', newLocation: "/man", topic: arg})
            }
        };
    }

    onItemClick(e) {
        let content = <ItemModalContent element={e.target.parentElement} closeFn={this.onModalClose}/>
        this.setState({modalContent: content})
    }

    onModalClose() {
        this.setState({modalContent: null})
    }

    Manual() {
        return <Man host={this.host} topic={this.state.topic}/>
    }

    Home() {

        let landscapes = this.state.landscapes;
        let content;
        if (!landscapes) {
            content = "loading";
        } else {
            content = landscapes.map(l => {
                return <div key={l.id}>
                    <div style={{width: '30%'}}>
                        <h2>{l.name}</h2>
                        Identifier: {l.identifier}<br/>
                        Contact: {l.contact || '-'}<br/>
                    </div>
                    <div style={{width: '70%'}}>
                        <Link to="/landscape">
                            <button className={'control'} onClick={() => this.enterLandscape(l)}>&rarr;</button>
                        </Link>
                    </div>
                </div>
            });
        }

        return (
            <div>
                <h1>Landscapes</h1>
                {content}
            </div>
        );
    }

    Landscape() {

        let landscape = this.state.landscape;
        if (landscape) {
            let data = this.host + '/render/' + landscape.identifier + '/map.svg';
            const {modalContent} = this.state;
            return <ReactSvgPanZoomLoader src={data} proxy={
                <>
                    <SvgLoaderSelectElement selector=".label" onClick={this.onItemClick}/>
                </>
            } render={(content) => (
                <div>
                    <Modal isOpen={modalContent !== null}
                           className="Modal"
                           overlayClassName="Overlay"
                           shouldCloseOnEsc={true}
                           contentLabel="Modal">{modalContent}</Modal>

                    <ReactSVGPanZoom key={'panzoom'}
                                     width={window.innerWidth * 0.95} height={window.innerHeight * 0.95}
                                     background={'transparent'}
                                     miniatureProps={{position: 'none'}} toolbarProps={{position: 'none'}}
                                     detectAutoPan={false}
                                     ref={Viewer => this.Viewer = Viewer}
                                     tool={this.state.tool} onChangeTool={tool => this.changeTool(tool)}
                                     value={this.state.value} onChangeValue={value => this.changeValue(value)}>
                        <svg>
                            {content}
                        </svg>
                    </ReactSVGPanZoom>
                </div>
            )}/>
        }
    }
}

export default App;
