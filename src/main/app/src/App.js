import React, {Component} from 'react';
import './App.css';
import {HexGrid, Layout, Hexagon, Text, Pattern, Path, Hex, GridGenerator, HexUtils} from 'react-hexgrid';
import map from './map';
import Nexagon from "./Nexagon";

class App extends Component {
    render() {
        const hexagons = GridGenerator.orientedRectangle(map.maxQ +5, 2*map.maxR);
        return (
            <div className="App">
                <HexGrid viewBox="600 -50 1000 1600" width="100%" height={1200}>
                    <Layout size={{x: 40, y: 40}} flat={true} spacing={1.1}>

                        {hexagons.map(hex => <Nexagon q={hex.q} r={hex.r} s={hex.s} className="other"/>)}
                        {map.items.map((vertex) =>
                            <Nexagon key={vertex.landscapeItem.identifier} q={vertex.hex.q} r={vertex.hex.r}
                                     s={vertex.hex.s} className={"service"} cellStyle={ {fill: vertex.groupColor} }>"
                                <Text>{vertex.landscapeItem.identifier}</Text>
                            </Nexagon>)
                        }


                    </Layout>
                </HexGrid>
            </div>
        );
    }
}

export default App;
