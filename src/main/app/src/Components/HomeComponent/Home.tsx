import React, { useState, useEffect, useContext, useCallback, ReactElement } from 'react';
import { Link } from 'react-router-dom';

import { ILandscape } from '../../interfaces';
import GenericModal from '../ModalComponent/GenericModal';
import LandscapeLog from '../LandscapeComponent/Log/LandscapeLog';
import Command from '../CommandComponent/Command';

import CommandContext from '../../Context/Command.context';
import LandscapeContext from '../../Context/Landscape.context';

import './Home.scss';

const Home: React.FC = () => {
  const [modalContent, setModalContent] = useState<string | ReactElement | ReactElement[] | null>(
    null
  );
  const [landscapes, setLandscapes] = useState<ILandscape[]>();
  const [loadLandscapes, setLoadLandscapes] = useState<boolean>(true);

  const commandContext = useContext(CommandContext);
  const landscapeContext = useContext(LandscapeContext);

  //Could be moved into useEffect but can be used for a reload button later on
  const getLandscapes = useCallback(async () => {
    if (loadLandscapes) {
      await fetch(process.env.REACT_APP_BACKEND_URL + '/api/')
        .then(response => {
          return response.json();
        })
        .then(json => {
          setLandscapes(json);
          setLoadLandscapes(false);
          landscapeContext.landscapes = json;
          commandContext.message = 'Loaded landscapes.';
        });
    }
  }, [commandContext.message, landscapeContext.landscapes, loadLandscapes]);

  //ComponentDidMount
  useEffect(() => {
    getLandscapes();
  }, [getLandscapes]);

  const enterLog = (l: ILandscape) => {
    setModalContent(<LandscapeLog landscape={l} />);
    commandContext.message = 'Showing log: ' + l.identifier;
  };

  const enterLandscape = (l: ILandscape) => {
    commandContext.message = 'Entering landscape: ' + l.identifier;
  };
  // Render
  let content: string | ReactElement[] = 'Loading landscapes...';
  if (landscapes) {
    content = landscapes.map(l => {
      return (
        <div key={l.identifier} className={'landscapeContainer'}>
          <div className='navigation'>
            <span className='title'>{l.name}</span>
            <Link to={`/landscape/${l.identifier}`}>
              <button className={'control'} onClick={() => enterLandscape(l)}>
                enter &gt;
              </button>
            </Link>
            <button className={'control'} onClick={() => enterLog(l)}>
              log
            </button>
          </div>
          <img className={'preview'} src={process.env.REACT_APP_BACKEND_URL + '/render/' + l.identifier + '/graph.png'} style={ {maxWidth: 100, float: 'left'}} />
          <blockquote>{l.description}</blockquote>
          <blockquote>
            Identifier: {l.identifier}
            <br />
            Contact: {l.contact || '-'}
            <br />
            Teams: {l.stats.teams.join(', ')}
            <br />
            Overall State: {l.stats.overallState || '-'}
            <br />
            {l.stats.items} items in {l.stats.groups} groups
            <br />
            Last update: {l.stats.lastUpdate || '-'}
            <br />
            <br />
            <a
              target={'_blank'}
              rel='noopener noreferrer'
              href={process.env.REACT_APP_BACKEND_URL + '/docs/' + l.identifier + '/report.html'}
            >
              Printable Report
            </a>
            &nbsp;
            <a
              target={'_blank'}
              rel='noopener noreferrer'
              href={process.env.REACT_APP_BACKEND_URL + '/render/' + l.identifier + '/map.svg'}
            >
              Printable Graph
            </a>
          </blockquote>

        </div>
      );
    });
  }

  return (
    <div className='homeContainer'>
      <GenericModal modalContent={modalContent} />
      <span className='header'>Landscapes</span>
      <div className='content'>{content}</div>
      <Command />
    </div>
  );
};

export default Home;
