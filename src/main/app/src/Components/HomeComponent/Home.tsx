import React, { useState, useEffect, useContext, ReactElement } from 'react';
import {Link} from "react-router-dom";

import {ILandscape} from "../../interfaces";
import GenericModal from "../ModalComponent/GenericModal";
import LandscapeLog from "../LandscapeComponent/LandscapeLog/LandscapeLog";
import Command from '../CommandComponent/Command';

import CommandContext from '../../Context/Command.context';

const Home: React.FC = () => {
    const [landscapes, setLandscapes] = useState<ILandscape[] | null>(null);
    const [modalContent, setModalContent] = useState<string | ReactElement | ReactElement[] | null>(null);

    // Needed for re-render, looking for another solution
    const commandContext = useContext(CommandContext);

    const getLandscapes = async () => {
        await fetch(process.env.REACT_APP_BACKEND_URL + "/api/")
            .then((response) => {
                return response.json()
            })
            .then((json) => {
                setLandscapes(json);
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
                    <Link to="/landscape">
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
        })}

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