import React, {Component} from 'react';
import './App.css';
import {HexGrid, Layout} from 'react-hexgrid';
import Nexagon from "./Nexagon";
import NPath from "./NPath";
import PathFinder from "./PathFinder";
import TilePath from "./TilePath";
import NPattern from "./NPattern";
import NGroup from "./NGroup";
import {INITIAL_VALUE, ReactSVGPanZoom, TOOL_AUTO} from 'react-svg-pan-zoom';

class App extends Component {

    Viewer = null;

    constructor(props, context) {
        super(props, context);
        this.state = {
            map: {},
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
        fetch(`http://localhost:8081/render/nivio:example/map.json`)
            .then((response) => {
                return response.json()
            })
            .then((json) => {
                this.setState({
                    map: json
                })
            });
    }

    render() {

        const size = 40;
        const padding = 10;

        let map = this.state.map;
        if (!map.items) {
            return <div>Hold tight while items are being fetched...</div>;
        }

        //const hexagons = GridGenerator.orientedRectangle(map.maxQ + 10, 3 * map.maxR);

        let byId = {};
        let occupied = [];

        map.items.map(vertex => {
            byId[vertex.id] = vertex;
            occupied.push(vertex.hex);
        });
        let pathFinder = new PathFinder(occupied);
        let maxX = map.width;
        let maxY = map.height;
        let viewBox = '-100 -100 ' + maxX * 4 + ' ' + maxY * 4;
        return (
            <div className="App">
                <button className="btn" onClick={() => this.zoomOnViewerCenter()}>Zoom in</button>
                <button className="btn" onClick={() => this.fitSelection()}>Zoom area 200x200</button>
                <button className="btn" onClick={() => this.fitToViewer()}>Fit</button>
                <ReactSVGPanZoom key={'panzoom'}
                    width={window.innerWidth * 0.95} height={window.innerHeight * 0.95} background={'white'}
                    miniatureProps={{position: 'none'}} toolbarProps={{position: 'none'}} detectAutoPan={false}
                    ref={Viewer => this.Viewer = Viewer}
                    tool={this.state.tool} onChangeTool={tool => this.changeTool(tool)}
                    value={this.state.value} onChangeValue={value => this.changeValue(value)}
                >
                    <HexGrid viewBox={viewBox} key={'viewbox'}>
                        <Layout key={'layout'} size={{x: size, y: size}} flat={true} spacing={1.1}>

                            {/*
                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"><Text>{hex.q + "," + hex.r}</Text></Nexagon>)}
                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"></Nexagon>)}
                        */}

                            {
                                map.groups.map(group => {
                                    map.items.filter(item => item.group === group.name);
                                    return (<NGroup key={group.name} start={group.start} end={group.end}
                                                    fill={group.color}/>);
                                })
                            }

                            {
                                map.items.map(vertex => {

                                        return vertex.relations.map(rel => {
                                            let paths = [];
                                            let path0 = new TilePath(vertex.hex);
                                            paths.push(path0);
                                            let target = byId[rel].hex;
                                            pathFinder.findPaths(paths, target);
                                            let path = pathFinder.sortAndFilterPaths(paths);
                                            return (<NPath tilePath={path} fill={vertex.color}/>);
                                        });
                                    }
                                )}

                            {map.items.map(vertex => {

                                let fill = '';
                                if (vertex.image)
                                    fill = btoa(vertex.id);

                                return (

                                    <Nexagon key={vertex.landscapeItem.identifier} q={vertex.hex.q} r={vertex.hex.r}
                                             s={vertex.hex.s} className={"service"} fill={fill}
                                             cellStyle={{stroke: vertex.color}}>
                                        <g>
                                            <rect x={size + padding} y={-10} rx="10" ry="10" fill={'white'} width={200}
                                                  height={20} className={'label'}/>
                                            <text x={size + padding + 100} y={5} width={200}
                                                  textAnchor="middle">{vertex.name}</text>

                                        </g>
                                    </Nexagon>
                                );
                            })
                            }

                        </Layout>
                        {map.items.map(vertex => {
                            if (vertex.image.length > 0) {
                                let fill = vertex.image;
                                let id = btoa(vertex.id);
                                return (
                                    <NPattern key={id} id={id} link={fill} size={{x: size - padding, y: size - padding}}
                                              padding={padding}></NPattern>);
                            }
                        })}

                    </HexGrid>
                </ReactSVGPanZoom>
            </div>
        );
    }
}

export default App;
