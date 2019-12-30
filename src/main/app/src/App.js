import React, {Component} from 'react';
import './App.css';
import {HexGrid, Layout, Text, Hex, GridGenerator, HexUtils} from 'react-hexgrid';
import Nexagon from "./Nexagon";
import NPath from "./NPath";
import PathFinder from "./PathFinder";
import TilePath from "./TilePath";
import NPattern from "./NPattern";

class App extends Component {

    constructor(props, context) {
        super(props, context);
        this.state = {
            map: {}
        }
    }

    componentDidMount() {
        this.getMap()
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

        let map = this.state.map;
        if (!map.items) {
            return <div>Hold tight while items are being fetched...</div>;
        }

        const hexagons = GridGenerator.orientedRectangle(map.maxQ + 10, 3 * map.maxR);

        let byId = {};
        let occupied = [];

        map.items.map(vertex => {
            byId[vertex.id] = vertex;
            occupied.push(vertex.hex);
        });
        let pathFinder = new PathFinder(occupied);

        return (
            <div className="App">
                <HexGrid viewBox="600 -50 1000 1600" width="100%" height={1200}>
                    <Layout size={{x: 40, y: 40}} flat={true} spacing={1.1}>

                        {/*
                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"><Text>{hex.q + "," + hex.r}</Text></Nexagon>)}
                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"></Nexagon>)}
                        */}

                        {
                            map.items.map(vertex => {

                            return vertex.relations.map( rel  => {
                                let paths =[];
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
                                                 s={vertex.hex.s} className={"service"} fill={fill} cellStyle={{stroke: vertex.color}}>"
                                    <text x="50" text-anchor="left">{vertex.name}</text>
                                    </Nexagon>
                                );
                        })
                        }

                    </Layout>
                    {map.items.map(vertex => {
                        if (vertex.image.length > 0) {
                            let fill = vertex.image;
                            let id = btoa(vertex.id);
                            let size = {x:30, y:30};
                            return (<NPattern id={id} link={fill} size={ size }></NPattern>);
                        }
                    })}
                </HexGrid>
            </div>
        );
    }
}

export default App;
