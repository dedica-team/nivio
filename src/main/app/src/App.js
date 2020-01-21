import React, {Component} from 'react';
import {ThemeProvider, createTheme, Arwes, Footer, Button, Words, Content, Loading} from 'arwes';
import {INITIAL_VALUE, ReactSVGPanZoom, TOOL_AUTO} from 'react-svg-pan-zoom';
import {ReactSvgPanZoomLoader, SvgLoaderSelectElement} from 'react-svg-pan-zoom-loader'
import {
    BrowserRouter as Router,
    Switch,
    Route,
    Link
} from "react-router-dom";

class App extends Component {

    Viewer = null;

    constructor(props, context) {
        super(props, context);
        this.state = {
            landscapes: null,
            landscape: null,
            tool: TOOL_AUTO,
            value: INITIAL_VALUE
        }

        this.host = window.location.host;
        let params = new URLSearchParams(window.location.search);
        let host = params.get('host');
        if (host !== null) {
            this.host = host;
        }
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
                    landscapes: json
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
                    mapData: json
                })
            });
    }

    onItemClick(l) {
        this.setState({landscape: l});
    }

    render() {
        return <Router>
            <ThemeProvider theme={createTheme()}>
                <Arwes>
                    <Switch>
                        <Route exact path="/" render={() => this.Home()}>
                        </Route>
                        <Route path="/landscape" render={() => this.Landscape()}>
                        </Route>
                    </Switch>
                    <Footer animate id={'footer'} style={{position: 'fixed', bottom: 0, width: '100%'}}>
                        <Link to="/"><Button animate>{'*'}</Button></Link>
                        <Words>{'Terminal here'}</Words>
                    </Footer>
                </Arwes>
            </ThemeProvider>
        </Router>


    }

    Home() {

        let landscapes = this.state.landscapes;
        let content;
        if (!landscapes) {
            content = <Loading animate/>;
        } else {
            content = landscapes.map(l => {
                return <Link
                    to="/landscape"><Button animate onClick={() => this.onItemClick(l)}>{l.name}</Button></Link>
            });
        }

        return (
            <Content style={{margin: 20}}>
                <h1>Landscapes</h1>
                {content}
            </Content>
        );
    }

    Landscape() {

        let landscape = this.state.landscape;
        let content;
        if (!landscape) {
            content = <Loading animate/>;
        } else {
            let data = this.host + '/render/' + landscape.identifier + '/map.svg';
            /*let proxy = {
            <>
                <SvgLoaderSelectElement selector="#tree" onClick={this.onItemClick}
                                        stroke={'#111111'}/>
            </>
        };

             */
            return <ReactSvgPanZoomLoader src={data} render={(content) => (
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
            )}/>

        }


    }
}

export default App;
