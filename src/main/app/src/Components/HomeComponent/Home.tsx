import React, { useState, useEffect, useContext, ReactElement } from 'react';
import {Link} from "react-router-dom";

import {ILandscape} from "../../interfaces";
import GenericModal from "../ModalComponent/GenericModal";
import LandscapeLog from "../LandscapeComponent/Log/LandscapeLog";
import Command from '../CommandComponent/Command';

import CommandContext from '../../Context/Command.context';
import LandscapeContext from '../../Context/Landscape.context';

const Home: React.FC = () => {
    const [modalContent, setModalContent] = useState<string | ReactElement | ReactElement[] | null>(null);
    const [landscapes, setLandscapes] = useState<ILandscape[]>();
 
    // Needed for re-render, looking for another solution
    const commandContext = useContext(CommandContext);
    const landscapeContext = useContext(LandscapeContext);

    /*
    TODO: Find a way to load all landscapes into context without having to access our home path
          if we reload e.g. http://localhost:3000/landscape/inout it wont load anymore
    */
    const getLandscapes = async () => {
        await fetch(process.env.REACT_APP_BACKEND_URL + "/api/")
            .then((response) => {
                return response.json()
            })
            .then((json) => {
                setLandscapes(json);
                landscapeContext.landscapes = json;
                commandContext.message = "Loaded landscapes.";
            });
    };

    //ComponentDidMount, only runs once because we provide []
    //remove warning with [getLandscapes] will trigger some unintended sideffects so we need to live with it for now
    useEffect(() => {
        getLandscapes();
    }, []);

    const onModalClose = () => {
        setModalContent(null);
    };

    const enterLog = (l: ILandscape) => {
        setModalContent(<LandscapeLog landscape={l} closeFn={onModalClose}/>);
        commandContext.message = "Showing log: " + l.identifier;
    };

    const enterLandscape = (l: ILandscape) => {
        commandContext.message = 'Entering landscape: ' + l.identifier;
    }
    // Render
    let content: string | ReactElement[] = "Loading landscapes...";
    if(landscapes){
        content = landscapes.map(l => {
            return <div key={l.identifier} className={"landscapeContainer"}>
                    <h2>{l.name}</h2>&nbsp;&nbsp;
                    <Link to={`/landscape/${l.identifier}`}>
                        <button className={'control'} onClick={() => enterLandscape(l)}>enter &gt;</button>
                    </Link>
                    &nbsp;
                    <button className={'control'} onClick={() => enterLog(l)}>log</button>
                    <blockquote>{l.description}</blockquote>
                    <blockquote>
                        Identifier: {l.identifier}<br/>
                        Contact: {l.contact || '-'}<br/>
                        Teams: {l.stats.teams.join(', ')}<br/>
                        Overall State: {l.stats.overallState || '-'}<br/>
                        {l.stats.items} items in {l.stats.groups} groups<br/>
                        Last update: {l.stats.lastUpdate || '-'}<br/><br/>
                        <a target={'_blank'} rel="noopener noreferrer" href={process.env.REACT_APP_BACKEND_URL + "/docs/" + l.identifier + "/report.html"}>Printable
                            Report</a>&nbsp;
                        <a target={'_blank'} rel="noopener noreferrer" href={process.env.REACT_APP_BACKEND_URL + "/render/" + l.identifier + "/map.svg"}>Printable
                            Graph</a><br/>
                    </blockquote>
                <br/>
                <br/>
            </div>
        })};

    return (
        <div>
            <GenericModal modalContent={modalContent}/>
            <h1>Landscapes</h1>
            {content}
            <Command/>
        </div>
    );    
};



export default Home;