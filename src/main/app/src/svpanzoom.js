import React, {Component} from 'react';

class App extends Component {

    Viewer = null;

    constructor(props, context) {
        super(props, context);
        this.state = {
            map: null,
            tool: TOOL_AUTO,
            value: INITIAL_VALUE,
            defaultZoom: false
        }
    }

    componentDidMount() {
        this.getMap();
    }

    changeValue(nextValue) {
        this.setState({value: nextValue})
    }

    fitToViewer() {
        this.Viewer.fitToViewer()
    }

    fitSelection() {
        this.Viewer.fitSelection(40, 40, 200, 200)
    }

    zoomOnViewerCenter() {
        this.Viewer.zoomOnViewerCenter(1.1)
    }

    getMap() {
        let params = new URLSearchParams(window.location.search);
        let data = params.get('data');
        if (data === undefined) {
            alert("data param missing");
            return;
        }

        fetch(data)
            .then((response) => {
                return response.text()
            })
            .then((text) => {
                this.setState({
                    map: text
                })
            });
    }

    onItemClick() {

    }

    render() {
        let map = this.state.map;
        if (!map) {
            return <div>Hold tight while map is being fetched...</div>;
        }

        let params = new URLSearchParams(window.location.search);
        let data = params.get('data');
        if (data === undefined) {
            alert("data param missing");
            return;
        }

        return (
            <ReactSvgPanZoomLoader src={data} proxy = {
                <>
                    <SvgLoaderSelectElement selector="#tree" onClick={this.onItemClick}
                                            stroke={'#111111'}/>
                </>
            } render= {(content) => (
                <div className="App">
                    <div style={ {float: 'right'}}>
                        <button className="btn" onClick={() => this.zoomOnViewerCenter()}>Zoom in</button>
                        <button className="btn" onClick={() => this.fitSelection()}>Zoom area 200x200</button>
                        <button className="btn" onClick={() => this.fitToViewer()}>Fit</button>
                    </div>
                    <ReactSVGPanZoom key={'panzoom'}
                                     width={window.innerWidth * 0.95} height={window.innerHeight * 0.95}
                                     background={'white'}
                                     miniatureProps={{position: 'none'}} toolbarProps={{position: 'none'}}
                                     detectAutoPan={false}
                                     ref={Viewer => this.Viewer = Viewer}
                                     tool={this.state.tool} onChangeTool={tool => this.changeTool(tool)}
                                     value={this.state.value} onChangeValue={value => this.changeValue(value)}>
                        <svg width={500} height={500} >
                            {content}
                        </svg>
                    </ReactSVGPanZoom>
                </div>
            )}/>
        );
    }
}

export default App;
