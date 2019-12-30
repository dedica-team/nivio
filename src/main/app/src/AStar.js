import {Text, Pattern, Hex, HexUtils} from 'react-hexgrid';



/*global Phaser*/
/* hexagonal A* pathfinding following http://www.policyalmanac.org/games/aStarTutorial.htm */

var bmpText;
var hexTileHeight = 61;
var hexTileWidth = 52;
var hexGrid;
var prevTile = new Phaser.Point();
var endTile;
var startTile = new Phaser.Point();
var nextTileToCall;
var showingPath;
var rootThree;
var sideLength;


function findPath(tile) {//passes in a hexTileNode
    console.log('exploring ' + tile.originali + ':' + tile.originalj);
    //tile.markDirty();
    if (Phaser.Point.equals(tile, endTile)) {
        //success, destination reached
        console.log('success');
        //now paint the path.
        paintPath(tile);
    } else {//find all neighbors
        var neighbors = getNeighbors(tile.originali, tile.convertedj);
        var newPt = new Phaser.Point();
        var hexTile;
        var totalCost = 0;
        var currentLowestCost = 100000;
        var nextTile;

        //find heuristics & cost for all neighbors
        while (neighbors.length) {
            newPt = neighbors.shift();
            hexTile = hexGrid.getByName("tile" + newPt.x + "_" + newPt.y);
            if (!hexTile.nodeClosed) {//if node was not already calculated
                //it should be tile.cost+10<hexTile.cost but it gets us trapped in caves
                if ((hexTile.nodeVisited && (tile.cost + 10) < hexTile.cost) ||
                    !hexTile.nodeVisited) {//if node was already visited, compare cost
                    hexTile.getHeuristic(endTile.originali, endTile.originalj);
                    hexTile.cost = tile.cost + 10;
                    hexTile.previousNode = tile;//point to previous node
                    hexTile.nodeVisited = true;
                    hexTile.showDifference();//display heuristic & cost
                } else continue;
                totalCost = hexTile.cost + hexTile.heuristic;
                if (totalCost < currentLowestCost) {//selct the next neighbour with lowest total cost
                    nextTile = hexTile;
                    currentLowestCost = totalCost;
                }
            } else {
                console.log('node closed');
            }
        }
        //console.log(tile.previousNode);
        tile.nodeClosed = true;
        if (nextTile != null) {
            findPath(nextTile);//call algo on the new tile
            nextTileToCall = nextTile;
        } else {
            if (tile.previousNode != null) {
                //current tile is now closed, open previous tile and redo.
                console.log('special call');
                tile.previousNode.cost -= 10;
                tile.previousNode.nodeClosed = false;
                findPath(tile.previousNode);//call algo on the previous tile
                nextTileToCall = tile.previousNode;
            } else {
                //no path
                nextTileToCall = null;
            }
        }
    }
}

function paintPath(tile) {
    tile.markDirty();
    if (tile.previousNode != null) {
        paintPath(tile.previousNode);
    }
}

/*
function costSort(a,b){
    //sort neighbours by tile cost so that we get better path-
    //non visited nodes come first,
    //visited nodes get sorted by cost where higher cost come first
    //closed nodes come last
    var hexTileA=hexGrid.getByName("tile"+a.x+"_"+a.y);
    var hexTileB=hexGrid.getByName("tile"+b.x+"_"+b.y);
    if(hexTileA.nodeClosed && hexTileB.nodeClosed){
        return 0;
    }else if(hexTileA.nodeClosed && !hexTileB.nodeClosed){
        return 1;
    }else if(!hexTileA.nodeClosed && hexTileB.nodeClosed){
        return -1;
    }else if(hexTileA.nodeVisited && hexTileB.nodeVisited){
        return hexTileB.cost-hexTileA.cost;
    }else if(!hexTileA.nodeVisited && !hexTileB.nodeVisited){
        return hexTileB.cost-hexTileA.cost;
    }else if(!hexTileA.nodeVisited && hexTileB.nodeVisited){
        return -1;
    }else if(hexTileA.nodeVisited && !hexTileB.nodeVisited){
        return 1;
    }
}*/
function checkForOccuppancy(i, j) {//check if the tile is outside effective area or has a mine
    var tileType = levelData[i][j];
    if (tileType == -1 || tileType == 10) {
        return true;
    }
    return false;
}

function checkforBoundary(i, j) {//check if the tile is outside level data array
    if (i < 0 || j < 0 || i > levelData.length - 1 || j > levelData[0].length - 1) {
        return true;
    }
    return false;
}

function populateNeighbor(i, j, tempArray) {//check & add new neighbor
    var nPoint = new Phaser.Point(i, j);
    nPoint = axialToOffset(nPoint);
    if (!checkforBoundary(nPoint.x, nPoint.y)) {
        if (!checkForOccuppancy(nPoint.x, nPoint.y)) {
            tempArray.push(nPoint.clone());
        }
    }
}
