import React, {Component} from 'react';
import {HexGrid, Layout} from 'react-hexgrid';
import Nexagon from "./Nexagon";
import NPath from "./NPath";
import PathFinder from "./PathFinder";
import TilePath from "./TilePath";
import NPattern from "./NPattern";
import NGroup from "./NGroup";
import NLabel from "./NLabel";
import {INITIAL_VALUE, ReactSVGPanZoom, TOOL_AUTO} from 'react-svg-pan-zoom';
import HexCoords from "./HexCoords";

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
        let params = new URLSearchParams(window.location.search);
        let landscape = params.get('landscape');
        if (landscape === undefined) {
            alert("landscape param missing");
            return;
        }

        fetch('http://localhost:8081/render/' + landscape + '/map.json')
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
        const css = require('./svg.css').toString();
        const size = 40;
        const padding = 10;

        let map = this.state.map;
        if (!map.items) {
            return <div>Hold tight while items are being fetched...</div>;
        }

        //const hexagons = GridGenerator.orientedRectangle(map.maxQ + 10, 3 * map.maxR);

        let byId = {};
        let occupied = [];

        map.items.forEach(vertex => {
            let hexCoords = new HexCoords(vertex.x, vertex.y, map.sizeFactor);
            vertex.hex = hexCoords.toHex();
            byId[vertex.id] = vertex;
            occupied.push(vertex.hex);
        });

        map.groups.forEach(group => {
            let hexCoords1 = new HexCoords(group.x1, group.y1, map.sizeFactor);
            let hexCoords2 = new HexCoords(group.x2, group.y2, map.sizeFactor);
            group.start = hexCoords1.toHex();
            group.end = hexCoords2.toHex();
        });
        let pathFinder = new PathFinder(occupied);
        let maxX = map.width;
        let maxY = map.height;
        let viewBox = '-100 -100 ' + maxX * 4 + ' ' + maxY * 4;
        document.title = map.landscape
        return (
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
                                 value={this.state.value} onChangeValue={value => this.changeValue(value)}
                >
                    <HexGrid viewBox={viewBox} key={'viewbox'}>
                        <style>
                            {css}
                        </style>
                        <Layout key={'layout'} size={{x: size, y: size}} flat={true} spacing={1.1}>

                            {/*
                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"><Text>{hex.q + "," + hex.r}</Text></Nexagon>)}
                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"></Nexagon>)}
                        */}

                            {
                                map.groups.map(group => {
                                    map.items.filter(item => item.group === group.name);
                                    return (<NGroup key={group.name} group={group}/>);
                                })
                            }

                            {
                                map.items.map(vertex => {

                                        return vertex.relations.map(rel => {
                                            let paths = [];
                                            let path0 = new TilePath(vertex.hex);
                                            paths.push(path0);
                                            let target = byId[rel.target].hex;
                                            pathFinder.findPaths(paths, target);
                                            let path = pathFinder.sortAndFilterPaths(paths);
                                            return (<NPath tilePath={path} fill={vertex.color} relation={rel}/>);
                                        });
                                    }
                                )}

                            {map.items.map(vertex => {

                                let fill = '';
                                if (vertex.image)
                                    fill = btoa(vertex.id);
                                const width = 200;
                                return (

                                    <Nexagon key={vertex.landscapeItem.identifier} q={vertex.hex.q} r={vertex.hex.r}
                                             s={vertex.hex.s} className={"service"} fill={fill} data={vertex}
                                             cellStyle={{stroke: vertex.color}}>
                                        <NLabel width={width} size={size} item={vertex} padding={padding}/>
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
                            return null;
                        })}

                    </HexGrid>
                </ReactSVGPanZoom>
            </div>
        );
    }
}

export default App;
