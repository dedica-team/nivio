import React, { Component } from 'react';
import './App.css';
import { HexGrid, Layout, Hexagon, Text, Pattern, Path, Hex, GridGenerator, HexUtils } from 'react-hexgrid';
import map from './map';

class App extends Component {
    render() {

        return (
            <div className="App">
                <HexGrid viewBox="-200 -200 400 400">
                    <Layout size={{ x: 30, y: 30 }} flat={true} spacing={1.1}>

                        {map.items.map( (vertex) =>
                            <Hexagon key={vertex.landscapeItem.identifier} q={vertex.hex.q} r={vertex.hex.r} s={vertex.hex.s}>
                            <Text>{vertex.landscapeItem.identifier}</Text>
                            </Hexagon>)
                        }
                    </Layout>
                </HexGrid>
            </div>
        );
    }
}

export default App;
